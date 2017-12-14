// Name: Yin-Hsia Yen
// USC NetID: 4337817705
// CS 455 PA1
// Fall 2017

/**
 * class CoinSimComponent
 * 
 * Called CoinTossSimulator and showed the result of tosses with 3 bars
 * User should input a positive number of trials for simulation
 * 
 */
import java.util.Scanner;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.*;

public class CoinSimComponent extends JComponent
{
   /*
   ** Instance variable:
   **       frameWidth/ frameHeight: get frame width & frame height before drawing
   **       numOfTwoHeads/ numOfTwoHeads/ numOfHeadTails/ numOfTrials: results from CoinTossSimulator
   */
   private int numOfTwoHeads;
   private int numOfTwoTails;
   private int numOfHeadTails;
   private int numOfTrials;
   private int frameWidth;
   private int frameHeight;

   private final int BAR_WIDTH = 50;
   private final int VERTICAL_BUFFER = 50;
   private final Color TWOHEADS_COLOR = Color.RED;
   private final Color HEADTAILS_COLOR = Color.GREEN;
   private final Color TWOTAILS_COLOR = Color.BLUE;

   /*
   ** Create a CoinSimComponent constructor and save the outcome from CoinTossSimulator
   ** Using an input reader to get the number of trials (local variable: numToss)
   ** Saving data from CoinTossSimulator ex. numOfTwoHeads, numOfTwoTails...  
   */
   public CoinSimComponent(){        

      CoinTossSimulator simulator = new CoinTossSimulator();
      Scanner in = new Scanner(System.in);

      int numToss = 0;
      do{
         System.out.print("Start the toss, please enter number of trials: ");
         numToss = in.nextInt();
         simulator.run(numToss);
      }while(numToss < 1);         

      numOfTwoHeads = simulator.getTwoHeads();
      numOfTwoTails = simulator.getTwoTails();
      numOfHeadTails = simulator.getHeadTails();
      numOfTrials = simulator.getNumTrials(); 
   }
   /*
   ** Draw 3 bars
   ** Calculated location, bar height,... for each bar and colored them
   **  
   ** local variable
   **    interval: located bars evenly
   **    left:     x position of a bar
   **    bottom:   start to draw a bar
   **    scale:    how tall in pixels is one trial on the bar
   **    height:   height of the bar when all the outcomes are the same
   **    ratio/percentage:    TwoHeads/ TwoTails/ HeadTails in all the trials
   **    str1/ str2/ str3:    label of each bar   
   */
   public void paintComponent(Graphics g)
   {  
      Graphics2D g2 = (Graphics2D) g;
    
      frameWidth = this.getSize().width;
      frameHeight = this.getSize().height;
      //System.out.println("[INFO] Component width: " + frameWidth + ", height: " + frameHeight);

      int interval = (frameWidth - 3* BAR_WIDTH) / 4;

      // bar1(Two Heads) 
      int left = interval;
      double ratio = (double)numOfTwoHeads / (double)numOfTrials ; 
      int height = frameHeight - 2 * VERTICAL_BUFFER ; 
      int percentage = (int)Math.round(ratio * 100);
      String str1 = "Two Heads: " + numOfTwoHeads + "(" + percentage + "%)";
      int bottom = frameHeight - VERTICAL_BUFFER - (int)(height * ratio);
      double scale = (double)height/numOfTrials;
      //Bar bar1 = new Bar(bottom, left, BAR_WIDTH, height, ratio, TWOHEADS_COLOR, str1);
      Bar bar1 = new Bar(bottom, left, BAR_WIDTH, numOfTwoHeads, scale, TWOHEADS_COLOR, str1);

      // bar2(Head_Tail)
      left = left + (BAR_WIDTH + interval);
      ratio = (double)numOfHeadTails / (double)numOfTrials ; 
      percentage = (int)Math.round(ratio * 100);
      String str2 = "Head Tails: " + numOfHeadTails + "(" + percentage + "%)";  
      bottom = frameHeight - VERTICAL_BUFFER - (int)(height * ratio);     
      //Bar bar2 = new Bar(bottom, left, BAR_WIDTH, height, ratio, HEADTAILS_COLOR, str2);
      Bar bar2 = new Bar(bottom, left, BAR_WIDTH, numOfHeadTails, scale, HEADTAILS_COLOR, str2);

      // bar3(Two Tails)  
      left = left + (BAR_WIDTH + interval);
      ratio = (double)numOfTwoTails / (double)numOfTrials ; 
      percentage = (int)Math.round(ratio * 100);
      String str3 = "Two Tails: " + numOfTwoTails + "(" + percentage + "%)";  
      bottom = frameHeight - VERTICAL_BUFFER - (int)(height * ratio);   
      //Bar bar3 = new Bar(bottom, left, BAR_WIDTH, height, ratio, TWOTAILS_COLOR, str3);
      Bar bar3 = new Bar(bottom, left, BAR_WIDTH, numOfTwoTails, scale, TWOTAILS_COLOR, str3);


      //System.out.println("[INFO] TwoHeads tosses: " + numOfTwoHeads + ", Trials: " + numOfTrials);
      //System.out.println("[INFO] TwoTails tosses: " + numOfTwoTails + ", Trials: " + numOfTrials);
      //System.out.println("[INFO] HeadTails tosses: " + numOfHeadTails + ", Trials: " + numOfTrials);        

      bar1.draw(g2);
      bar2.draw(g2);
      bar3.draw(g2);
   }
}