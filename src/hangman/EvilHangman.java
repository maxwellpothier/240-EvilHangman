package hangman;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {
    public static void main(String[] args) throws IOException, GuessAlreadyMadeException, EmptyDictionaryException {
        // Declaring main variables
        String dictionaryStr = args[0];
        File dictionary = new File(dictionaryStr);
        Scanner scan = new Scanner(System.in);
        int numLetters = Integer.parseInt(args[1]);
        int numGuesses = Integer.parseInt(args[2]);
        boolean wonGame = false;

        // Instance of the game class
        EvilHangmanGame game = new EvilHangmanGame();
        game.set_guessesLeft(numGuesses);
        // STARTING GAME
        game.startGame(dictionary, numLetters);
        while (game.get_guessesLeft() > 0) {
            System.out.println("You have " + game.get_guessesLeft() + " guesses left");
            System.out.print("Used Letters: ");
            for (char parse : game.getGuesses()) {
                System.out.print(parse);
                System.out.print(" ");
            }
            System.out.println();
            System.out.print("Word: ");
            System.out.println(game.getKey());
            char guess = getUserInput();
            while (game.getGuesses().contains(Character.toLowerCase(guess))) {
                System.out.println("You already used that letter");
                guess = getUserInput();
            }
            Set<String> guessedWords = game.makeGuess(guess);
            if (guessedWords.size() == 1){
                String correctKey = null;
                for (String word: guessedWords){
                    correctKey = word;
                    break;
                }
                boolean guessed = true;
                for (int i = 0; i < correctKey.length(); i++) {
                    if (!game.getGuesses().contains(correctKey.charAt(i)))
                        guessed = false;
                }
                if (guessed) {
                    System.out.println("You Won!");
                    System.out.println("The correct word was " + correctKey);
                    wonGame = true;
                    break;
                }
            }
            int count= 0;
            for (int i=0; i < game.getKey().length(); i++) {
                if (game.getKey().charAt(i) == guess) {
                    count++;
                }
            }
            if (count == 0) {
                System.out.println("Sorry, there are no " + guess + "\'s");
                game.set_guessesLeft(game.get_guessesLeft() - 1);
            } else {
                System.out.println("Yes, there is " + count + " " + guess);
            }
        } // USER HAS NO MORE GUESSES.


        if (!wonGame) {
            String winningWord = null;
            for (String word: game.getSet()){
                winningWord = word;
                break;
            }
            System.out.println("You lose!");
            System.out.println("The word was: " + winningWord);
        }
        scan.close();
    }


    private static char getUserInput() throws IOException {
        char guess;
        System.out.print("Enter guess: ");
        BufferedReader myReader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        input = myReader.readLine();
        if (input.length() != 1){
            System.out.println("Invalid guess.");
            guess =getUserInput();
        } else {
            guess = input.charAt(0);
        }
        if (!Character.isUpperCase(guess) && !Character.isLowerCase(guess)) {
            System.out.println("Invalid input");
            guess = getUserInput();
        }
        return guess;
    }

}
