// Name: Yin-Hsia Yen
// USC NetID: 4337817705
// CS 455 PA1
// Fall 2017

// we included the import statements for you
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * Bar class
 * A labeled bar that can serve as a single bar in a bar graph.
 * The text for the label is centered under the bar.
 * 
 * NOTE: we have provided the public interface for this class. Do not change
 * the public interface. You can add private instance variables, constants,
 * and private methods to the class. You will also be completing the
 * implementation of the methods given.
 * 
 */
public class Bar {
   
   private int vLen;
   private int wLen;
   private int bWidth;
   private int bHeight;
   private Color bColor;
   private String bLabel;

   private final Color SUBTITLE_COLOR = Color.BLACK;

   /**
      Creates a labeled bar.  You give the height of the bar in application
      units (e.g., population of a particular state), and then a scale for how
      tall to display it on the screen (parameter scale). 
  
      @param bottom  location of the bottom of the label
      @param left  location of the left side of the bar
      @param width  width of the bar (in pixels)
      @param barHeight  height of the bar in application units
      @param scale  how many pixels per application unit
      @param color  the color of the bar
      @param label  the label at the bottom of the bar
   */
   public Bar(int bottom, int left, int width, int barHeight,
              double scale, Color color, String label) {

      vLen = bottom;
      wLen = left;
      bWidth = width;
      bHeight = (int)Math.round(barHeight * scale);
      bColor = color;
      bLabel = label;
   }
   
   /**
      Draw the labeled bar. 
      @param g2  the graphics context
   */
   public void draw(Graphics2D g2) {

      // Draw a bar
      g2.setColor(bColor);
      /*
         Rectangle rect = new Rectangle(wLen, vLen, bWidth, bHeight);
         g2.fill(rect);
         System.out.println("[DEBUG] x: " + rect.x + " y: " + rect.y + " bHeight: " + bHeight);
      */
   
      Rectangle rectTest = new Rectangle(wLen, vLen, bWidth, bHeight);
      g2.fill(rectTest);
      //System.out.println("[DEBUG] x: " + rectTest.x + " y: " + rectTest.y);

      // subtitle
      g2.setColor(SUBTITLE_COLOR);
      Font font = g2.getFont();
      FontRenderContext context = g2.getFontRenderContext();
      Rectangle2D labelBounds = font.getStringBounds(bLabel, context);
      int widthOfLabel = (int)labelBounds.getWidth();
      int heightOfLabel = (int)labelBounds.getHeight();
      int shift = 0;
      shift = (bWidth - widthOfLabel) / 2;  
      g2.drawString(bLabel, wLen + shift, vLen + bHeight + heightOfLabel);       

   }
}