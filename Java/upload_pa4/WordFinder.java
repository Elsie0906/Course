// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CS 455 PA4
// Fall 2017


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;
import java.io.*;

/**
 * WordFinder class
 * 
 * Given letters that could comprise a Scrabble rack
 * Creates a list of all legal words that can be formed from the letters on that rack
 * Display all such words, with the corresponding Scrabble score for each word, in decreasing order by score
 *
 * E.g. 
 *    Rack? cmal
 *    We can make 11 words from "aclm"
 *    All of the words with their scores (sorted by score):
 *    8: calm
 *    8: clam
 *    7: cam
 *    7: mac
 *    5: lac
 *    5: lam
 *    5: mal
 *    4: am
 *    4: ma
 *    2: al
 *    2: la
 */

public class WordFinder {

  // Exit the program by typing in "."
  private static final String ENDFLAG = ".";
 
/**
* Inner Class Scrabble, with two instance variables: string(str) and integer(score)
* Use this class for sorting 
* 
* Get string and score from methods: getString() and getScore()
* Instance variables:
*   str: get anagrams from a given rack(read from file or user input)
*   score: points that string can get
*   ex. user input: "dotwgoo"
*       str: wood, score: 8
**/   
  private static class Scrabble{
      private String str;
      private int score;

/**
* Constructor: save string name and its scores
**/
      private Scrabble(String str, int value){ 
        this.str = new String(str);
        score = value;
      }

/**
* @return string name
**/
      public String getString(){
        String str = new String(this.str);
        return str;
      }

/**
* @return scrabble score for a given string
**/
      public int getScore(){
        int val = score;
        return val;
      }
  }

/**
* made a new comparator method for sorting
*   1. Display all words, with the corresponding Scrabble score for each word, in decreasing order by score
*   2. For words with the same scrabble score, the words must appear in alphabetical order 
* @return 
*   negative value, for entry1 should display before entry2; vice versa, positive value for entry1 should display later 
* 
**/

  private static class ScrabbleComparator implements Comparator<Scrabble>{
      public int compare(Scrabble entry1, Scrabble entry2){

        String str1 = entry1.getString();
        String str2 = entry2.getString();

        if( entry1.getScore() > entry2.getScore()){
            return -1;
        }else if(entry1.getScore() < entry2.getScore()){
            return 1;
        }else
            return str1.compareTo(str2);
      }
  }

/**
* Responsible for processing the command-line argument, and handling errors processing
*
* How to call it from the command line: (4 modes)
*   1. java WordFinder
*      look-up in a default dictionary(sowpods.txt), and rack: read from keyboard(user input)
*   2. java WordFinder dictionaryFile
*      look-up in a specific dictionary, rack: read from keyboard
*   3. java WordFinder test1.in test1.out
*      look-up in a default dictionary(sowpods.txt), rack: read from file
*   4. java WordFinder tinyDictionary.txt tiny.in tiny.out
*      look-up in a specific dictionary, rack: read from file
*
* Error checking: 
*   If the dictionary file given is not found, print out an err message
**/
  public static void main(String[] args)  {    
      String fileName = "sowpods.txt", inputFile = "", outputFile = "";
      try 
      {
        switch( args.length){
            case 0:                            // default dictionary: sowpods.txt, rack: read from keyboard
                runScrabbleGame(fileName);
                break;
            case 1:
                fileName = args[0];            // specific dictionary, rack: read from keyboard
                runScrabbleGame(fileName);
                break;         
            case 2:
                inputFile = args[0];           // default dictionary: sowpods.txt, rack: read from file
                outputFile = args[1];
                runScrabbleGame(fileName, inputFile, outputFile);
                break;
            case 3:
                fileName = args[0];            // specific dictionary, rack: read from file
                inputFile = args[1];
                outputFile = args[2];
                runScrabbleGame(fileName, inputFile, outputFile);
                break;
            default:                           // at most 3 parameters: dictionary, input file, output file
                System.out.println("ERROR: incorrect command line argument"); 
                break;
        }
      }      
      catch (FileNotFoundException exc) {
          System.out.println("ERROR: File not found: " + fileName);
      }     
  }

/**
* Run a Scarabble game (used for mode 3 and 4; that is, rack is read from file)
* 1. Pre-processing dictionary ( @param fileName)
* 2. Using method readData() to read rack from file(@param inputFile)
* 3. Find all anagrams for a given rack by using method getAllAnagrams()
* 4. Check corresponding Scrabble score for each words with method findScore()
* 5. Write data(result) into file( @param outputFile)
* @param fileName    dictionary file
* @param inputFile   rack file
* @param outputFile  scores for each rack and write data into this output file
* @throws FileNotFoundException  if the file is not found
**/

  private static void runScrabbleGame(String fileName, String inputFile, String outputFile) throws FileNotFoundException {

      AnagramDictionary dictionary = new AnagramDictionary(fileName);

      String str;
      char mark = '"';
      Scanner in = new Scanner(new File(inputFile));
      PrintWriter out = new PrintWriter(outputFile);

      do{
          out.write("Type . to quit. \n");
          out.write("Rack? ");

          str = readData(in);

          if( !str.equals(ENDFLAG)){
              ArrayList<String> arrlist = getAllAnagrams(dictionary, str);
              String key = mark + AnagramDictionary.canonical(str) + mark;
              out.write("We can make " + arrlist.size() + " words from " + key + "\n");              
              if( arrlist.size() > 0){    
                  ArrayList<Scrabble> list = findScore(arrlist);
                  getResult(list, out);                
              }
          }
      }while(!str.equals(ENDFLAG));

      in.close();
      out.close();
  }

/**
* Run a Scarabble game (used for mode 1 and 2; that is, rack is read from user input)
* 1. Pre-processing dictionary ( @param fileName)
* 2. Using method readData() to read rack from keyboard
* 3. Find all anagrams for a given rack by using method getAllAnagrams()
* 4. Check corresponding Scrabble score for each words with method findScore()
* 5. Write data(result) into file
* @param fileName    dictionary file
* @throws FileNotFoundException  if the file is not found
**/

  private static void runScrabbleGame(String fileName) throws FileNotFoundException {

      AnagramDictionary dictionary = new AnagramDictionary(fileName);

      String str;
      char mark = '"';

      Scanner in = new Scanner(System.in);

      do{
          System.out.println("Type . to quit.");
          System.out.print("Rack? ");

          str = readData(in);

          if( !str.equals(ENDFLAG)){
              ArrayList<String> arrlist = getAllAnagrams(dictionary, str);
              String key = mark + AnagramDictionary.canonical(str) + mark;
              System.out.println("We can make " + arrlist.size() + " words from " + key);              
              if( arrlist.size() > 0){    
                  ArrayList<Scrabble> list = findScore(arrlist);
                  getResult(list);                
              }
          }
      }while(!str.equals(ENDFLAG));

  }

/**
* 1. Sorting with a user-defined comparator
* 2. List all string and its corresponding Scrabble score into an output file
**/

  private static void getResult(ArrayList<Scrabble> list, PrintWriter out) throws FileNotFoundException{

      Collections.sort(list, new ScrabbleComparator());  

      out.write("All of the words with their scores (sorted by score):\n");

      for(Scrabble element : list) {
          out.write(element.getScore() + ": " + element.getString() + "\n");
      } 
  }
/**
* 1. Sorting with a user-defined comparator
* 2. Print out all string and its corresponding Scrabble score on the console
**/
  private static void getResult(ArrayList<Scrabble> list){

      Collections.sort(list, new ScrabbleComparator());  

      System.out.println("All of the words with their scores (sorted by score):");

      for(Scrabble element : list) {
          System.out.println(element.getScore() + ": " + element.getString());
      } 
  }

/**
* Read a line for a rack
**/

  private static String readData(Scanner in){

      String str = "";

      if( in.hasNextLine()){
          str = in.nextLine();
      }

      return str;

  }

/**
* Find all subsets of a given rack, and list all corresponding anagrams
* @param dictionary  
*    look-up in this dictionary
* @param str
*    a given rack read from file or user input
* @return 
*    all anagrams of a given rack that we can find in the dictionary
**/
  private static ArrayList<String> getAllAnagrams(AnagramDictionary dictionary, String str){

      ArrayList<String> arrlist = new ArrayList<>();
      Rack rack = new Rack();

      ArrayList<String> sets = rack.getSubsets(str);

      //System.out.println("[DEBUG] getSubsets: " + sets.size());

      for(int i=0; i<sets.size(); i++){
          arrlist.addAll(dictionary.getAnagramsOf(sets.get(i)));
      }

      return arrlist;       
  }

/**
* record all words and its corresponding score (look-up in ScoreTable)
* @param arrlist
*     all anagrams of a given rack that we can find in the dictionary
* @return 
*     all words and its corresponding score in an object arrlist    
**/
  private static ArrayList<Scrabble> findScore(ArrayList<String> arrlist){

      ArrayList<Scrabble> list = new ArrayList<>();

      for(String element : arrlist) {
          int score = ScoreTable.getScore(element);
          //System.out.println(score + ": " + element);
          Scrabble scrabble = new Scrabble(element, score);
          list.add(scrabble);
      }

      return list;
  }


}
