// Name: Yin-Hsia Yen
// USC NetID: yinhsiay 
// USC ID: 4337817705
// CSCI455 PA2
// Fall 2017

import java.util.ArrayList;
import java.util.Random;
import java.util.*;

/*
   class SolitaireBoard
   The board for Bulgarian Solitaire.  You can change what the total number of cards is for the game
   by changing NUM_FINAL_PILES, below.  Don't change CARD_TOTAL directly, because there are only some values
   for CARD_TOTAL that result in a game that terminates.
   (See comments below next to named constant declarations for more details on this.)
 */


public class SolitaireBoard {
   
   public static final int NUM_FINAL_PILES = 9;
   // number of piles in a final configuration
   // (note: if NUM_FINAL_PILES is 9, then CARD_TOTAL below will be 45)
   
   public static final int CARD_TOTAL = NUM_FINAL_PILES * (NUM_FINAL_PILES + 1) / 2;
   // bulgarian solitaire only terminates if CARD_TOTAL is a triangular number.
   // see: http://en.wikipedia.org/wiki/Bulgarian_solitaire for more details
   // the above formula is the closed form for 1 + 2 + 3 + . . . + NUM_FINAL_PILES
   public static final int MIN_VALUE_EACH_PILES = 1;
   // minimum valid number for each pile

    // Note to students: you may not use an ArrayList -- see assgt description for details.
   
   
   /**
      Representation invariant:
      1. All values should be in the interval of [MIN_VALUE_PILES, CARD_TOTAL]
      2. Sum of value should be CARD_TOTAL 

      Instant variable:
        nums: Using an Array to store all input values with size CARD_TOTAL
              No need to resize it later 
        pile: Define number of piles from random generator or user
    */
   
   // <add instance variables here>
      private int[] nums = new int[CARD_TOTAL];
      private int pile;
 
   /**
     Creates a solitaire board with the configuration specified in piles.
     piles has the number of cards in the first pile, then the number of cards in the second pile, etc.
     PRE: piles contains a sequence of positive numbers that sum to SolitaireBoard.CARD_TOTAL
   */
   public SolitaireBoard(ArrayList<Integer> piles) {

      pile = piles.size();

      for(int i=0; i<pile; i++){
        nums[i] = piles.get(i);
      }

      assert isValidSolitaireBoard();   // sample assert statement (you will be adding more of these calls)
   }
 
   
   /**
      Creates a solitaire board with a random initial configuration.
      generator: create a random generator which gives value from MIN_VALUE_EACH_PILES to CARD_TOTAL
      num:  Random value for each pile
      total: sum of value in the array should be CARD_TOTAL 
   */
   public SolitaireBoard() {

      Random generator = new Random();

      int total = 0, num = 0, idx = 0;
      // Get several random values
      while(total != CARD_TOTAL){
          num = MIN_VALUE_EACH_PILES + generator.nextInt(CARD_TOTAL);
          total += num;
          if(total > CARD_TOTAL){
            total -= num;
            nums[idx] = CARD_TOTAL - total;
            total = CARD_TOTAL;
          }
          else
            nums[idx] = num;

          idx++;
      }

      pile = idx;

      // check array value
      assert isValidSolitaireBoard();
   }
  
   
   /**
      Plays one round of Bulgarian solitaire.  Updates the configuration according to the rules
      of Bulgarian solitaire: Takes one card from each pile, and puts them all together in a new pile.
      The old piles that are left will be in the same relative order as before, 
      and the new pile will be at the end.

      [Description]
      temp: decrease nums[i] by 1
      Scard empty pile (temp = 0) in a more efficient way with index "index"
      Save valid index range in the array "nums" with instant variable "pile"; the valid index: 0 ~ (pile - 1)
    */
   public void playRound() {
      int index = 0;
      int temp = 0;
      for(int i=0; i<pile; i++){
        temp = nums[i] - 1;      // take one card from each pile

        if(temp >= MIN_VALUE_EACH_PILES){
          nums[index] = temp;
          index ++;              // Increment valid index range
        }
      }

      nums[index] = pile;        // new pile
      pile = index + 1;

      // check array value
      assert isValidSolitaireBoard();      
   }
   
   /**
      Returns true iff the current board is at the end of the game.  That is, there are NUM_FINAL_PILES
      piles that are of sizes 1, 2, 3, . . . , NUM_FINAL_PILES, in any order.
      Method1: 
              Using an Array with size = NUM_FINAL_PILES; mark it as we found the number
              ex. x = 5, A[x] = true
              And then check if all values in the array are marked
      Method2: (Implemented!) 
              Minimum value of sum of 9 distict integer num(>=1) equals to 45(CARD_TOTAL)       
              Using a hashset to check if there are duplicated value
    */
   
   public boolean isDone() {
      boolean reVal = true;

      if(pile < NUM_FINAL_PILES){
          // check array value
          assert isValidSolitaireBoard();
          return false;
      }

      HashSet<Integer> hs = new HashSet<>();

      for(int i=0; i<NUM_FINAL_PILES; i++){
          reVal = hs.add(nums[i]);
          if(reVal == false){
            // check array value
            assert isValidSolitaireBoard();            
            return false;
          }
      }   

      // check array value
      assert isValidSolitaireBoard();

      return reVal;  
   }

   
   /**
      Returns current board configuration as a string with the format of
      a space-separated list of numbers with no leading or trailing spaces.
      The numbers represent the number of cards in each non-empty pile.
      Local variable:
      s   Convert integer to string (https://docs.oracle.com/javase/tutorial/java/data/converting.html)
      str cascade each element (s)
    */
   public String configString() {
      String str = new String();

      str = Integer.toString(nums[0]);

      for(int i=1; i<pile; i++){
        String s = Integer.toString(nums[i]);
        str = str + " " + s;
        //str = str + " " + nums[i];
      }

      // check array value
      assert isValidSolitaireBoard();

      return str;        
   }
   
   
   /**
      Returns true iff the solitaire board data is in a valid state
      (See representation invariant comment for more details.)
      1. All values, nums[i], should be in the interval of [1,SolitaireBoard.CARD_TOTAL]
      2. Sum of value should be SolitaireBoard.CARD_TOTAL
      local variable:
      total  sum of value in the array; return false when total != SolitaireBoard.CARD_TOTAL
    */
   private boolean isValidSolitaireBoard() {

      int total = 0;

      for(int i=0; i<pile; i++){
        if(nums[i] < MIN_VALUE_EACH_PILES || nums[i] > CARD_TOTAL)
          return false;

        total += nums[i];
      }

      if(total != CARD_TOTAL)
        return false;      

      return true;  

   }
   

    // <add any additional private methods here>


}
