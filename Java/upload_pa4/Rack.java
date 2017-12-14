// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CS 455 PA4
// Fall 2017

import java.util.ArrayList;
import java.util.*;
import java.util.Arrays;
import java.lang.*;

/**
 * A Rack of Scrabble tiles
 */

public class Rack {

    /**
    * Instance variables
    **/
    private String rack;                        // current rack
    private HashMap<Character, Integer> hm;     // how many different chars in the rack & its repetition

    /**
    * Constructor: 
    *   save current rack and create a hashmap 
    **/

    public Rack(){

      rack = new String();
      hm = new HashMap<>();

    }

    /**
    * @return current rack
    **/

    public String getCurrentRack(){

      String str = new String(rack); // make a copy, do not return private data directly

      return str;
    }

    /**
    * Using a hashmap to store current rack
    * Used for getSubsets(), change rack into a different format
    * hm< key, value >
    * key: different char shown in a rack (at most 26 different char a to z)
    * value: repetition of this char
    * ex. rack "aabbbd" -> key = 'a', value = 2; key = 'b', value = 3, and so on.
    **/

    private void changeFormat(){

      hm.clear(); // Step1, make sure hashmap is clean

      for(int i=0; i<rack.length(); i++){

          char ch = rack.charAt(i);

          if( !hm.containsKey(ch)){
              hm.put(ch, new Integer(1));
          }
          else{
              int num = (int) hm.get(ch);
              hm.put(ch, new Integer(num + 1));
          }  
      }
    }

    /**
    * turn rack into (string, int[]) format and get all subsets for the rack
    * ex. rack "aabbbd" -> 
    *     String unique = "abd"; 
    *     int[] mult = {2, 3, 1};
    *
    * @param str
    *    a given rack
    * @return 
    *    all subsets of a given rack
    **/

    public ArrayList<String> getSubsets(String str){

      rack = new String(str);

      changeFormat();

      String unique = "";
      int[] mult = new int[hm.size()];

      Set set = hm.entrySet();
      Iterator i = set.iterator();
      int idx = 0;

      while(i.hasNext()){
        Map.Entry me = (Map.Entry) i.next();
        String s = Character.toString((char)me.getKey());
        unique = unique + s;
        mult[idx] = (int)me.getValue();
        idx++;
      }

      ArrayList<String> tempSet = allSubsets(unique, mult, 0);   // get all subsets, k start from 0
      ArrayList<String> sets = new ArrayList<>();                // make a copy, do not return private data directly

      for (String element : tempSet) {
          if( !element.equals(""))                               // remove empty word
              sets.add(element);
      }
      return sets; 
    }

/*
    // For RackTester, understand how allSubsets() works
    
    public ArrayList<String> getSubsets(String unique, int[] mult, int k){

      // example to show relation between values in unique and mult:
      for (int i = 0; i < unique.length(); i++) {
          System.out.println(unique.charAt(i) + " appears " + mult[i] + " times in the rack");
      }   

      ArrayList<String> sets = allSubsets(unique, mult, k);

      return sets;   

    }
*/

    /**
    * Finds all subsets of the multiset starting at position k in unique and mult.
    * unique and mult describe a multiset such that mult[i] is the multiplicity of the char
    *      unique.charAt(i).
    * PRE: mult.length must be at least as big as unique.length()
    *      0 <= k <= unique.length()
    * @param unique a string of unique letters
    * @param mult the multiplicity of each letter from unique.  
    * @param k the smallest index of unique and mult to consider.
    * @return all subsets of the indicated multiset
    * @author Claire Bono
    */
    private static ArrayList<String> allSubsets(String unique, int[] mult, int k) {
      ArrayList<String> allCombos = new ArrayList<>();
      
      if (k == unique.length()) {  // multiset is empty
         allCombos.add("");
         return allCombos;
      }
      
      // get all subsets of the multiset without the first unique char
      ArrayList<String> restCombos = allSubsets(unique, mult, k+1);
      
      // prepend all possible numbers of the first char (i.e., the one at position k) 
      // to the front of each string in restCombos.  Suppose that char is 'a'...
      
      String firstPart = "";          // in outer loop firstPart takes on the values: "", "a", "aa", ...
      for (int n = 0; n <= mult[k]; n++) {   
         for (int i = 0; i < restCombos.size(); i++) {  // for each of the subsets 
                                                        // we found in the recursive call
            // create and add a new string with n 'a's in front of that subset
            allCombos.add(firstPart + restCombos.get(i));  
         }
         firstPart += unique.charAt(k);  // append another instance of 'a' to the first part
      }
      
      return allCombos;
    }

   
}
