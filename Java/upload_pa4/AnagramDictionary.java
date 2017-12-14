// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CS 455 PA4
// Fall 2017

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.*;
import java.util.*;
import java.util.Arrays;


/**
 * A dictionary of all anagram sets. 
 * Note: the processing is case-sensitive; so if the dictionary has all lower
 * case words, you will likely want any string you test to have all lower case
 * letters too, and likewise if the dictionary words are all upper case.
 */

public class AnagramDictionary {
 
  /**
   * Instance variable:
   *    a hashmap used for pre-processing dictionary, add all unique words in this hashmap
   */   
  private HashMap<String, ArrayList<String>> hm;

  /**
   * Create an anagram dictionary from the list of words given in the file
   * indicated by fileName.  
   * PRE: The strings in the file are unique.
   * @param fileName  the name of the file to read from
   * @throws FileNotFoundException  if the file is not found
   */
  public AnagramDictionary(String fileName) throws FileNotFoundException {

      Scanner in = new Scanner(System.in);
      
      File inFile = new File(fileName);

      try (Scanner input = new Scanner(inFile))
      {
          //System.out.println("open file: " + fileName);

          hm = new HashMap<>();

          preProcessing(input);
      }

      catch (FileNotFoundException exception)
      {
          throw new FileNotFoundException(fileName);
      }
  }

  /**
   * Approach 2 mentioned in this assignment (using a hashmap for fast look-up)
   * Pre-processing words in a dictionary before we start a Scrabble Game
   * 
   * Add each different words in a hashmap < key, value>
   * key: canonoical form of a given word
   * value: word
   * E.g. word = "calm" , its canonical form = "aclm"
   * @param input
   *     read words from a dictionary 
   * 
   */   
  private void preProcessing(Scanner input){

      String str = readData(input);

      while(str != null){
          String key = canonical(str);

          if( !hm.containsKey(key)){
              ArrayList<String> list = new ArrayList<String>();
              list.add(str);
              hm.put(key, list);
          }
          else{
              ArrayList<String> list = hm.get(key);   
              list.add(str);
              hm.put(key, list);
          }           

          str = readData(input);
      }

  }
  /**
   * Instead of finding all permutation of a given word, we put words into canonical form
   * That is, canonical form will be a sorted version of the characters in the word
   * ex. "calm" and "clam" both have canonical form in "aclm"
   * 
   * [case-sensitive] don't need to cange format into lowercase
   * E.g., if the dictionary given has only upper-case versions of words, 
   *       it will find words from a rack such as "CMAL", but won't be able to find any words from the rack "cmal"
   * @return
   *     a sorted version of the characters in the word
   */
  public static String canonical(String str){

      //String strLowerCase = str.toLowerCase();
      //char[] charArray = strLowerCase.toCharArray();

      String word = new String(str);

      char[] charArray = word.toCharArray();
      Arrays.sort(charArray);

      String sortedString = new String(charArray);

      //System.out.println("original: " + str + ", canonical: " + sortedString);

      return sortedString;

  }

  /**
   * read words from file
   */

  private String readData(Scanner in){

      if( in.hasNextLine()){
          String str = in.nextLine(); 
          return str; 
      }
      
      return null;

  }

  /**
   * Get all anagrams of the given string. This method is case-sensitive.
   * E.g. "CARE" and "race" would not be recognized as anagrams.
   * @param s string to process
   * @return a list of the anagrams of s
   * 
   */
  public ArrayList<String> getAnagramsOf(String s) {

      ArrayList<String> findAnagram = new ArrayList<String>();      

      // step1: find canonical form of the string

      String key = canonical(s);

      if( !hm.containsKey(key)){
          //System.out.println("[DEBUG] cannot find word in the dictionary");
          return findAnagram;
      }
      else{
          ArrayList<String> list = hm.get(key); 
          findAnagram.addAll(list);
      }

      return  findAnagram;
  }
   
   
}
