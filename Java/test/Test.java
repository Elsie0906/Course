// Name: Yin-Hsia Yen
// USC NetID: 4337817705
// CS 455 PA1
// Fall 2017

/**
 * class CoinTossSimulatorTester
 * 
 * Simulates trials of tossing two coins and allows the user to access the
 * cumulative results.
 * 
 * Test 1: using input reader to simulate the toss
 *         error handling: negative input is not allowed! Users need to try again
 * 
 * Test 2: rest all the value, and run a constant number of trials (= 100) twice
 *         check if the outcome is a random number  
 *
 * Test 3: rest all the value, and run a constant number of trials (= 100) twice
 *         check the outcome of culmulated toss
 */
import java.util.Scanner;

public class CoinTossSimulatorTester
{
   public static void main(String[] args)
   {
      CoinTossSimulator simulator = new CoinTossSimulator();
      Scanner in = new Scanner(System.in);


      // Test 1:
      int numToss;
      do{
         System.out.print("Start the toss, please enter number of trials: ");
         numToss = in.nextInt();
         simulator.run(numToss);
      }while(numToss < 1);
      
      System.out.println("Total number of trials: " + simulator.getNumTrials() + ", Expected: " + numToss);
      System.out.println("Total number of twoHeads: " + simulator.getTwoHeads());     
      System.out.println("Total number of twoTails: " + simulator.getTwoTails());
      System.out.println("Total number of HeadTails: " + simulator.getHeadTails());
      System.out.println("Expected: getNumTrials() = getTwoHeads() + getTwoTails() + getHeadTails()");
      System.out.println();

      // Test 2: 

      System.out.print("Reset!! ");
      simulator.reset();
      System.out.println("Expected all instance variables to be zero");
      System.out.println("Total number of trials: " + simulator.getNumTrials());
      System.out.println("Total number of twoHeads: " + simulator.getTwoHeads());      
      System.out.println("Total number of twoTails: " + simulator.getTwoTails());
      System.out.println("Total number of HeadTails: " + simulator.getHeadTails());     

      System.out.println("Start a new trial: ");
      numToss = 100;
      simulator.run(numToss);
      System.out.println("Total number of trials: " + simulator.getNumTrials() + ", Expected: 100");
      System.out.println("Total number of twoHeads: " + simulator.getTwoHeads());      
      System.out.println("Total number of twoTails: " + simulator.getTwoTails());
      System.out.println("Total number of HeadTails: " + simulator.getHeadTails()); 
      System.out.println("Expected: getNumTrials() = getTwoHeads() + getTwoTails() + getHeadTails()");
      int allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails(); 
      System.out.println("getTwoHeads() + getTwoTails() + getHeadTails() = " + allResult);

      simulator.reset();
      System.out.println("Start a new trial: ");
      numToss = 100;
      simulator.run(numToss);
      System.out.println("Total number of trials: " + simulator.getNumTrials() + ", Expected: 100");
      System.out.println("Total number of twoHeads: " + simulator.getTwoHeads());      
      System.out.println("Total number of twoTails: " + simulator.getTwoTails());
      System.out.println("Total number of HeadTails: " + simulator.getHeadTails()); 
      System.out.println("Expected: getNumTrials() = getTwoHeads() + getTwoTails() + getHeadTails()");
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails(); 
      System.out.println("getTwoHeads() + getTwoTails() + getHeadTails() = " + allResult);
      System.out.println();

      // Test 3:
      simulator.reset();
      System.out.println("Start a new trial: ");
      numToss = 300;
      simulator.run(numToss);
      System.out.println("Total number of trials: " + simulator.getNumTrials() + ", Expected: 300");
      System.out.println("Total number of twoHeads: " + simulator.getTwoHeads());      
      System.out.println("Total number of twoTails: " + simulator.getTwoTails());
      System.out.println("Total number of HeadTails: " + simulator.getHeadTails()); 
      System.out.println("Expected: getNumTrials() = getTwoHeads() + getTwoTails() + getHeadTails()");
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails(); 
      System.out.println("getTwoHeads() + getTwoTails() + getHeadTails() = " + allResult);

      System.out.println("Adding a new trial: ");
      numToss = 200;
      simulator.run(numToss);
      System.out.println("Total number of trials: " + simulator.getNumTrials() + ", Expected: 500");
      System.out.println("Total number of twoHeads: " + simulator.getTwoHeads());      
      System.out.println("Total number of twoTails: " + simulator.getTwoTails());
      System.out.println("Total number of HeadTails: " + simulator.getHeadTails()); 
      System.out.println("Expected: getNumTrials() = getTwoHeads() + getTwoTails() + getHeadTails()");
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails(); 
      System.out.println("getTwoHeads() + getTwoTails() + getHeadTails() = " + allResult);
   }
}