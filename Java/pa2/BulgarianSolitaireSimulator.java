// Name: Yin-Hsia Yen
// USC NetID: yinhsiay 
// USC ID: 4337817705
// CSCI455 PA2
// Fall 2017

import java.util.ArrayList;
import java.util.Scanner;

/**
   class BulgarianSolitaireSimulator

   Simulate solitaire game with 4 different mode
   Show up intial configuration by calling private method initialConfig()
   Process solitaire game by calling private method runSolitaireBoard()
   local variable:
      userconfig    You may have initial configuration from user or random generator w/o "-u"
      singleConfig  You may stop between every round of the game w/o "-s"  
      scanner       Create a scanner to get info from user
 */

public class BulgarianSolitaireSimulator {
   public static void main(String[] args) {
      boolean singleStep = false;
      boolean userConfig = false;

      Scanner in = new Scanner(System.in);

      for (int i = 0; i < args.length; i++) {
         if (args[i].equals("-u")) {
            userConfig = true;
         }
         else if (args[i].equals("-s")) {
            singleStep = true;
         }
      }

      System.out.println("Number of total cards is " + SolitaireBoard.CARD_TOTAL);

      SolitaireBoard solitaire = initialConfig(userConfig, in);

      runSolitaire(singleStep, in, solitaire);        
   }
   
/**
   Run a simulation for solitaire game
   You need to stop when the option -s turened on and wait for user hits the return key
   Call Solitaire.playRound() for every round and promp out current configuration by calling Solitaire.configString()
   Check if the game finished with Solitaire.isDone()
   local variable:
      finish   check if the current status is at the end of the game
      step     how may steps do we have until the game finished
*/
   private static void runSolitaire(boolean config, Scanner input, SolitaireBoard board){
      int step = 1;
      boolean finish = false;

      while(!finish)
      {

         board.playRound();
         System.out.println("[" + step + "] Current configuration: " + board.configString());

         finish = board.isDone();

         // promp out message and wait for user response when the option -s turned on
         if(config == true){
            System.out.print("<Type return to continue>");
            input.nextLine();        
         }

         // promp out message when the game finished
         if(finish == true)
            System.out.println("Done!");

         step++;
      }
   }
/**
   Create a Solitaire and initialize its configuration from user or random generator
   Ask user to try again until all input values are valid
   promp out initial configuration before the game started
   You may check the option -u here for calling different Solitaire constructor
   local vairable:
      lineScanner  read a line and save input values into an arrayList
      list         Using an arrayList to store initial values from user
      board        create a solitaire 
      check        error-checking for input values
*/   
   private static SolitaireBoard initialConfig(boolean config, Scanner input){
      SolitaireBoard board = new SolitaireBoard();
      boolean check = false;

      if(config == true){
         ArrayList<Integer> list = new ArrayList<Integer>();
         while(!check){
            
            System.out.println("Please enter a space-separated list of positive integers followed by newline: "); 
            Scanner lineScanner = new Scanner(input.nextLine());
            while(lineScanner.hasNextInt()){
               int value = lineScanner.nextInt();
               list.add(value);
            }         

            check = isValidInput(list);

            if(check == false){
               list = new ArrayList<Integer>();   
               System.out.println("ERROR: Each pile must have at least one card and the total number of cards must be " + SolitaireBoard.CARD_TOTAL);
            }       
         }

         board = new SolitaireBoard(list);
      }

      System.out.println("Initial configuration: " + board.configString());

      return board;
   }
/**
   Error-checking; check if the input value is valid
   Invariant:
      1. All values should be in the interval of [MIN_VALUE_PILES, CARD_TOTAL], ex. [1,45] for 9 piles
      2. Sum of value should be SolitaireBoard.CARD_TOTAL
   local variable:
      total  sum of input value       
*/   
   private static boolean isValidInput(ArrayList<Integer> arrayList){
      int total = 0;

      for(int i=0; i<arrayList.size(); i++){

         if(arrayList.get(i) < SolitaireBoard.MIN_VALUE_EACH_PILES || arrayList.get(i) > SolitaireBoard.CARD_TOTAL)
            return false;

         total += arrayList.get(i);

         if(total > SolitaireBoard.CARD_TOTAL)
            return false;
      }

      if(total != SolitaireBoard.CARD_TOTAL)
         return false;
      
      return true; 
   }

  
}
