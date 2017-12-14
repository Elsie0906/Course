// Name: Yin-Hsia Yen
// USC NetID: 4337817705
// CS 455 PA1
// Fall 2017

/**
 * class CoinSimComponentListener
 * 
 * Detecting change of the window size
 */
import java.awt.event.*;
import java.awt.*;

public class CoinSimComponentListener implements ComponentListener
{

      public void componentHidden(ComponentEvent e) {}
      public void componentMoved(ComponentEvent e) {}
      public void componentShown(ComponentEvent e) {}

      public void componentResized(ComponentEvent e) {
            Dimension newSize = e.getComponent().getBounds().getSize(); 
            System.out.println("ComponentResized Event: width " + newSize.width + ", height " + newSize.height);        
      } 

}