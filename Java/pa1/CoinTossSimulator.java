// Name: Yin-Hsia Yen
// USC NetID: 4337817705
// CS 455 PA1
// Fall 2017

/**
 * class CoinTossSimulator
 * 
 * Simulates trials of tossing two coins and allows the user to access the
 * cumulative results.
 * 
 * NOTE: we have provided the public interface for this class.  Do not change
 * the public interface.  You can add private instance variables, constants, 
 * and private methods to the class.  You will also be completing the 
 * implementation of the methods given. 
 * 
 * Invariant: getNumTrials() = getTwoHeads() + getTwoTails() + getHeadTails()
 * 
 */

import java.util.Random;

public class CoinTossSimulator {
   private Random generator;
   private final int SIDES = 4;
   private int numOfTrials;
   private int numOfTwoHeads;
   private int numOfTwoTails;
   private int numOfHeadTails;

   /**
      Creates a coin toss simulator with no trials done yet.

      Instance variable:
        generator      create a random generator to simulate the result of toss
                       ex. num = 1 -> representative of result "TwoHeads"  
        numOfTrials    cumulated number of trials for simulating
        numOfTwoHeads  cumulated number of result "TwoHeads"
        numOfTwoTails  cumulated number of result "TwoTails"
        numOfHeadTails cumulated number of result "one Head &  one Tail"

   */
   public CoinTossSimulator() {
      generator = new Random();
      numOfTrials = 0;
      numOfTwoHeads = 0;
      numOfTwoTails = 0;
      numOfHeadTails = 0;
   }


   /**
      Runs the simulation for numTrials more trials. Multiple calls to this method
      without a reset() between them *add* these trials to the current simulation.

      Using a random number generator from 1~4 for simulating the result of the toss
      Details are described belowed.
      
      @param numTrials  number of trials to for simulation; must be >= 1

      local variable:
        toss      simulation results of the toss
                  ex. '1' -> representative of result "TwoHeads" 
                      '2' -> representative of result  "one Head" and then "one Tail"
                      '3' -> representative of result  "one Tail" and then "one Head"
                      '4' -> representative of result "TwoTails" 
    */
   public void run(int numTrials) {
      if( numTrials < 1 ){
          System.out.println("numTrials must be >= 1, Please try again");
          return;
      }

      //System.out.println("adding number of trials: " + numTrials);

      numOfTrials += numTrials;

      for(int i=0; i<numTrials; i++){
          int toss = 1 + generator.nextInt(SIDES);

          if(toss == 1){ // two head
            numOfTwoHeads += 1;
          }else if(toss == 2 || toss == 3){ // one head & one tail
            numOfHeadTails += 1;
          }else{ // two tail
            numOfTwoTails += 1;
          }
      }
      //System.out.println();
   }


   /**
      Get number of trials performed since last reset.
   */
   public int getNumTrials() {
      return numOfTrials;
   }


   /**
      Get number of trials that came up two heads since last reset.
   */
   public int getTwoHeads() {
      return numOfTwoHeads;
   }


   /**
     Get number of trials that came up two tails since last reset.
   */  
   public int getTwoTails() {
      return numOfTwoTails;
   }


   /**
     Get number of trials that came up one head and one tail since last reset.
   */
   public int getHeadTails() {
      return numOfHeadTails;
   }


   /**
      Resets the simulation, so that subsequent runs start from 0 trials done.
    */
   public void reset() {
      numOfTrials = 0;
      numOfTwoHeads = 0;
      numOfTwoTails = 0;
      numOfHeadTails = 0;
   }

}