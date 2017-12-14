// Name: Yin-Hsia Yen
// USC NetID: 4337817705
// CS 455 PA1
// Fall 2017

/**
 * class CoinSimViewer
 * 
 * Simulates trials of tossing two coins and shows the result in bars
 *
 * In this main function, create a Frame (default: 800 * 500),and a Component (Optional: ComponentListener)
 *
 * In class CoinSimComponent, called CoinTossSimulator and showed the result of tosses with 3 bars
 *
 * How to detect window size changed?
 * Method 1:
 * Using class CoinSimComponentListener to get information when window size changed
 * When window size changed, called paintComponent again
 * Method 2: (Chosen)
 * When window size changed, paintComponent get called automatically 
 * Modified frame size in paintComponent  
 */
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

public class CoinSimViewer
{
   // default size of window: 800 * 500
   private final static int DEFAULT_WIDTH = 800;
   private final static int DEFAULT_HEIGHT = 500;

   // Instance variable: 
   //saving last presented frame width & frame height
   private static int frame_width;
   private static int frame_height;
/*
   private static class CoinSimComponentListener extends ComponentAdapter{
      public void componentResized(ComponentEvent e) {
            Dimension newSize = e.getComponent().getBounds().getSize(); 
            //System.out.println("ComponentResized Event: width " + newSize.width + ", height " + newSize.height);       

            if((frame_width != newSize.width) || (frame_height != newSize.height)){
               e.getComponent().repaint();
            } 
      }
   }
*/   
   public static void main(String[] args)
   {

      JFrame frame = new JFrame();

      frame_width = DEFAULT_WIDTH;
      frame_height = DEFAULT_HEIGHT;
      frame.setSize(frame_width,frame_height);
      frame.setTitle("CoinSim");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      CoinSimComponent component = new CoinSimComponent();
      frame.add(component);    

      //frame.addComponentListener(new CoinSimComponentListener());

      frame.setVisible(true);

   }
}