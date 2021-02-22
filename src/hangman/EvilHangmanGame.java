package hangman;

import java.io.File;
import java.util.Set;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
  // Declaring game variables
  private Set<String> set;
  private ArrayList<Character> guesses;
  private int _guessesLeft;
  private Map<String, Set<String>> map;
  private String key;
  private String initString;

  // CONSTRUCTOR
  public EvilHangmanGame() { }

  // MAIN FUNCTIONS
  @Override
  public void startGame(File dictionary, int wordLength) throws FileNotFoundException, EmptyDictionaryException {
    // Init declared variables
    set = new HashSet<>();
    guesses = new ArrayList<>();
    map = new HashMap<>();
    // Populate Set with words in the input file.
    Scanner scan = new Scanner(new FileInputStream(dictionary.getPath()));
    if (!scan.hasNext()) {
      throw new EmptyDictionaryException();
    }
    while (scan.hasNext()) {
      String dictionaryEntry=scan.next();
      if (dictionaryEntry.length() == wordLength) {
        set.add(dictionaryEntry);
      }
    }
    if (set.size() == 0) {
      throw new EmptyDictionaryException();
    }
    scan.close();
    // Create string of dashes
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < wordLength; i++) {
      str.append('-');
    }
    key = str.toString();
    initString = str.toString();
  }
  @Override
  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
    if (Character.isUpperCase(guess)) {
      guess = Character.toLowerCase(guess);
    }
    if (guesses.contains(guess)) {
      throw new GuessAlreadyMadeException();
    } else {
      guesses.add(guess);
      populateMap(guess);
      if (map.containsKey(initString) && map.size() == 1) {
        updateSet(map);
      } else {
        // Find the map index with the largest size
        Map<String, Set<String>> tempMap=new HashMap<>();
        int longest = 0;
        for (String tKey : map.keySet()) {
          if (map.get(tKey).size() > longest) {
            tempMap.clear();
            tempMap.put(tKey, map.get(tKey));
            longest = map.get(tKey).size();
          }
          if (map.get(tKey).size() == longest) {
            tempMap.put(tKey, map.get(tKey));
          }
        }
        if (tempMap.size() == 1) {
          updateSet(tempMap);
        } else {
          // Map 2 will implement the second priority
          Map<String, Set<String>> map2 = new HashMap<>();
          boolean alreadyProcessed = false;
          for (String tKey : tempMap.keySet()) {
            for (int i = 0; i < tKey.length(); i++) {
              if (tKey.charAt(i) != '-') {
                alreadyProcessed = true;
              }
            }
            if (!alreadyProcessed) {
              map2.put(tKey, tempMap.get(tKey));
            }
          }
          if (map2.size() == 1) {
            updateSet(map2);
          } else {
            int minRepeats = 100;
            for (String tKey : tempMap.keySet()) {
              int repeated = 0;
              for (int i = 0; i < tKey.length(); i++) {
                if (tKey.charAt(i) != '-') {
                  repeated++;
                }
              }
              if (repeated < minRepeats) {
                minRepeats = repeated;
                map2.clear(); // Need to clear out the last most repeated.
                map2.put(tKey, tempMap.get(tKey));
              }
              // If it is tied, also add it to the map.
              if (repeated == minRepeats) {
                map2.put(tKey, tempMap.get(tKey));
              }
            }
            // Map 3 will implement the third priority
            Map<String, Set<String>> map3 = new HashMap<>();
            if (map2.size() != 1) {
              for (int i = key.length() - 1; i >= 0; i--) {
                for (String tKey : map2.keySet()) {
                  if (tKey.charAt(i) != '-') {
                    map3.put(tKey, map2.get(tKey));
                  }
                }
                if (map3.size() == 1) {
                  break;
                } else if (map3.size() > 0) {
                  map3.clear();
                }
              }
            } else {
              map3 = map2;
            }
            updateSet(map3);
          }
        }
      }
      return set;
    }
  }

  // GETTER AND SETTER FUNCTIONS
  public Set<String> getSet() {
    return set;
  }
  public void setSet(Set<String> set) {
    this.set=set;
  }
  public ArrayList<Character> getGuesses() {
    Collections.sort(guesses);
    return guesses;
  }
  public void setGuesses(ArrayList<Character> guesses) {
    this.guesses=guesses;
  }
  public int get_guessesLeft() {
    return _guessesLeft;
  }
  public void set_guessesLeft(int _guessesLeft) {
    this._guessesLeft=_guessesLeft;
  }
  public Map<String, Set<String>> getMap() {
    return map;
  }
  public void setMap(Map<String, Set<String>> map) {
    this.map = map;
  }
  public String getKey() {
    return key;
  }
  public void setKey(String guessedKey) {
    if (key == "")
      key = guessedKey;
    else {
      for (int i=0; i < guessedKey.length(); i++) {
        if (key.charAt(i) == '-') {
          char[] tempArray=new char[guessedKey.length()];
          tempArray=key.toCharArray();
          tempArray[i]=guessedKey.charAt(i);
          key=new String(tempArray);
        }
      }
    }
  }
  public String getInitString() {
    return initString;
  }
  public void setInitString(String initString) {
    this.initString=initString;
  }



  @Override
  public SortedSet<Character> getGuessedLetters() {
    return null;
  }


  // HELPER FUNCTIONS
  private void populateMap(char guess) {
    for (String parse : set) {
      parse = parse.toLowerCase();
      StringBuilder appendChars =new StringBuilder();
      for (int i = 0; i < parse.length(); i++) {
        if (parse.charAt(i) != guess) {
          appendChars.append('-');
        } else
          appendChars.append(guess);
      }
      if (map.containsKey(appendChars.toString())) {
        map.get(appendChars.toString()).add(parse);
      } else {
        Set<String> miniSet = new HashSet<>();
        miniSet.add(parse);
        map.put(appendChars.toString(), miniSet);
      }
      appendChars.setLength(0);
    }
  }

  private void updateSet(Map<String, Set<String>> tempMap) {
    String str = "";
    for (Map.Entry<String, Set<String>> entry : tempMap.entrySet()) {
      set = entry.getValue();
      str = entry.getKey();
    }
    map.clear();
    setKey(str);
  }
}