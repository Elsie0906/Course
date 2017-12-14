// Name: Yin-Hsia Yen
// USC NetID: 4337817705
// CS 455 PA1
// Fall 2017

/**
 * class CoinTossSimulatorTester (Unit Test Program)
 * 
 * Simulates trials of tossing two coins and allows the user to access the
 * cumulative results.
 * Called each method of CoinTossSimulator once and checked the result if it is correct
 * Special case:
 * error-checking   an invalid number of trials the program will print out an informative error message
 *                  and then ask user to try again
 * 
 */
import java.util.Scanner;

public class CoinTossSimulatorTester
{
   public static void main(String[] args)
   {
      CoinTossSimulator simulator = new CoinTossSimulator();

      int allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails();
      System.out.println("After constructor: ");
      System.out.println("Number of trials [exp: 0]: " + allResult);
      System.out.println("TwoHeads tosses: " + simulator.getTwoHeads());     
      System.out.println("TwoTails tosses: " + simulator.getTwoTails());
      System.out.println("One-head one-tail tosses: " + simulator.getHeadTails());
      boolean check = (simulator.getNumTrials() == allResult) ? true : false ;
      System.out.println("Tosses add up correctly? " + check);
      System.out.println();

      System.out.println("After run(-1): ");
      simulator.run(-1);
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails();
      System.out.println("Number of trials [exp: 0]: " + allResult);
      System.out.println("TwoHeads tosses: " + simulator.getTwoHeads());     
      System.out.println("TwoTails tosses: " + simulator.getTwoTails());
      System.out.println("One-head one-tail tosses: " + simulator.getHeadTails());
      check = (simulator.getNumTrials() == allResult) ? true : false ;
      System.out.println("Tosses add up correctly? " + check);
      System.out.println();

      simulator.run(1);
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails();
      System.out.println("After run(1): ");
      System.out.println("Number of trials [exp: 1]: " + allResult);
      System.out.println("TwoHeads tosses: " + simulator.getTwoHeads());     
      System.out.println("TwoTails tosses: " + simulator.getTwoTails());
      System.out.println("One-head one-tail tosses: " + simulator.getHeadTails());
      check = (simulator.getNumTrials() == allResult) ? true : false ;
      System.out.println("Tosses add up correctly? " + check);
      System.out.println();

      simulator.run(10);
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails();
      System.out.println("After run(10): ");
      System.out.println("Number of trials [exp: 11]: " + allResult);
      System.out.println("TwoHeads tosses: " + simulator.getTwoHeads());     
      System.out.println("TwoTails tosses: " + simulator.getTwoTails());
      System.out.println("One-head one-tail tosses: " + simulator.getHeadTails());
      check = (simulator.getNumTrials() == allResult) ? true : false ;
      System.out.println("Tosses add up correctly? " + check);
      System.out.println();

      simulator.run(100);
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails();
      System.out.println("After run(100): ");
      System.out.println("Number of trials [exp: 111]: " + allResult);
      System.out.println("TwoHeads tosses: " + simulator.getTwoHeads());     
      System.out.println("TwoTails tosses: " + simulator.getTwoTails());
      System.out.println("One-head one-tail tosses: " + simulator.getHeadTails());
      check = (simulator.getNumTrials() == allResult) ? true : false ;
      System.out.println("Tosses add up correctly? " + check);
      System.out.println();

      simulator.reset();
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails();
      System.out.println("After reset: ");
      System.out.println("Number of trials [exp: 0]: " + allResult);
      System.out.println("TwoHeads tosses: " + simulator.getTwoHeads());     
      System.out.println("TwoTails tosses: " + simulator.getTwoTails());
      System.out.println("One-head one-tail tosses: " + simulator.getHeadTails());
      check = (simulator.getNumTrials() == allResult) ? true : false ;
      System.out.println("Tosses add up correctly? " + check);
      System.out.println();

      simulator.run(1000);
      allResult = simulator.getTwoHeads() + simulator.getTwoTails() + simulator.getHeadTails();
      System.out.println("After run(1000): ");
      System.out.println("Number of trials [exp: 1000]: " + allResult);
      System.out.println("TwoHeads tosses: " + simulator.getTwoHeads());     
      System.out.println("TwoTails tosses: " + simulator.getTwoTails());
      System.out.println("One-head one-tail tosses: " + simulator.getHeadTails());
      check = (simulator.getNumTrials() == allResult) ? true : false ;
      System.out.println("Tosses add up correctly? " + check);
      System.out.println();
   }
}