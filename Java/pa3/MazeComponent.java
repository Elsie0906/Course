// Name: Yin-Hsia Yen
// USC loginid: yinhsiay 
// CS 455 PA3
// Fall 2017

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.*;
import javax.swing.JComponent;
import java.util.LinkedList;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

/**
   MazeComponent class
   
   A component that displays the maze and path through it if one has been found.
*/
public class MazeComponent extends JComponent
{

   private static final int START_X = 10;    // top left of corner of maze in frame
   private static final int START_Y = 10;
   private static final int BOX_WIDTH = 20;  // width and height of one maze "location"
   private static final int BOX_HEIGHT = 20;
   private static final int INSET = 2;       // how much smaller on each side to make entry/exit inner box

   private final Color FRAME = Color.BLACK;  // color for invisible boundary
   private final Color WALL = Color.BLACK;   // color for walls
   private final Color START = Color.YELLOW; // color for entry loc of the maze
   private final Color END = Color.GREEN;    // color for exit loc of the maze
   private final Color ROUTE = Color.BLUE;   // color for path out of the maze

   /*
   ** Instance variable:
                   maze   get info from class Maze
   */
   
   private Maze maze;
   
   /**
      Constructs the component.
      @param maze   the maze to display
   */
   public MazeComponent(Maze maze) 
   {   
      this.maze = maze;

      // [DEBUG]
      //maze.disPlay();
   }

   
   /**
     Draws the current state of maze including the path through it if one has
     been found.
     @param g the graphics context

     private method:
        drawInvisibleWall    Invisible wall/boundary for maze
        drawAWall            Walls for maze
        labelEntry           Start point of the maze
        labelExit            End point of the maze
        findRoute            Display the route out of the maze if there is one
   */
   public void paintComponent(Graphics g)
   {
      // DEBUG
      //this.maze.disPlay();

      Graphics2D g2 = (Graphics2D) g;

      // Frame(Invisible wall) for maze
      drawInvisibleWall(g2);

      // Walls for maze
      drawAWall(g2);

      // start point: a smaller block
      labelEntry(g2);

      // end point: a smaller block
      labelExit(g2);

      // route
      findRoute(g2);
   }

   /*
   ** Denoting the entry location (with yellow block) are intentionally a little smaller than the other blocks
   ** block size: (BOX_WIDTH - INSET * 2) * (BOX_HEIGHT - INSET * 2) 
   **
   ** @param g2   the graphic context
   */
   private void labelEntry(Graphics2D g2){

      MazeCoord strLoc = maze.getEntryLoc();

      if(strLoc != null){
        int x = START_X + BOX_WIDTH * strLoc.getCol() + INSET;
        int y = START_Y + BOX_HEIGHT * strLoc.getRow() + INSET;
        int width = BOX_WIDTH - INSET * 2;
        int height = BOX_HEIGHT - INSET * 2;
        drawABrick(g2,x,y,width,height,START);
      }    
   }

   /*
   ** Denoting the exit location (with green block) are intentionally a little smaller than the other blocks
   ** block size: (BOX_WIDTH - INSET * 2) * (BOX_HEIGHT - INSET * 2) 
   **
   ** @param g2   the graphic context
   */

   private void labelExit(Graphics2D g2){
      MazeCoord endLoc = maze.getExitLoc();

      if(endLoc != null){
        int x = START_X + BOX_WIDTH * endLoc.getCol() + INSET;
        int y = START_Y + BOX_HEIGHT * endLoc.getRow() + INSET;
        int width = BOX_WIDTH - INSET * 2;
        int height = BOX_HEIGHT - INSET * 2;
        drawABrick(g2,x,y,width,height,END);
      }
   }

   /*
   ** A virtual border around the maze
   ** border size: (BOX_WIDTH * maze.numRows()) * (BOX_HEIGHT * maze.numCols())
   **
   ** @param g2   the graphic context
   */

   private void drawInvisibleWall(Graphics2D g2){

      int m = maze.numRows();
      int n = maze.numCols();

      // Frame(Invisible wall) for maze
      g2.setColor(FRAME);
      Rectangle rect = new Rectangle(START_X, START_Y, BOX_HEIGHT * n, BOX_WIDTH * m);       
      g2.draw(rect);

   }

   /*
   ** Display the maze with a path through it shown in blue, 
   ** or just the original maze if there is no possible path from the entrance to the exit.
   ** 
   ** If path != null -> there is a path can be found out of the maze
   ** Blue line connected between the center of two blocks: (x1,y1) to (x2,y2) 
   **
   ** @param g2       The graphic context   
   ** local variable:
   **            x1   The X coordinate of the start point of the line segment
   **            y1   The Y coordinate of the start point of the line segment
   **            x2   The X coordinate of the end point of the line segment
   **            y2   The Y coordinate of the end point of the line segment    
   */
   private void findRoute(Graphics2D g2){

      LinkedList<MazeCoord> path = maze.getPath();

      if( path != null){
          for(int i=1; i<path.size(); i++){
              MazeCoord node = path.get(i-1);
              int row = node.getRow();
              int col = node.getCol();
              double x1 = START_X + BOX_WIDTH * col + BOX_WIDTH/2;
              double y1 = START_Y + BOX_HEIGHT * row + BOX_HEIGHT/2;

              node = path.get(i);
              row = node.getRow();
              col = node.getCol();
              double x2 = START_X + BOX_WIDTH * col + BOX_WIDTH/2;
              double y2 = START_Y + BOX_HEIGHT * row + BOX_HEIGHT/2;

              Point2D.Double from = new Point2D.Double(x1, y1); 
              Point2D.Double to = new Point2D.Double(x2, y2);
              Line2D.Double route = new Line2D.Double(from, to);

              g2.setColor(ROUTE);
              g2.draw(route);              
          }
      }      
   }

   /*
   ** Display walls in the maze with black blocks; check with method hasWallAt() from maze class
   ** If there is a wall at this location(i,j), put a black block
   ** Block size: BOX_WIDTH * BOX_HEIGHT
   **
   ** @param g2   the graphic context    
   */
   private void drawAWall(Graphics2D g2){

      int m = maze.numRows();
      int n = maze.numCols();

      for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){

            MazeCoord loc = new MazeCoord(i,j);

            if(maze.hasWallAt(loc) == true){
              int x = START_X + j * BOX_WIDTH;
              int y = START_Y + i * BOX_HEIGHT;       
              drawABrick(g2, x, y, BOX_WIDTH, BOX_HEIGHT, WALL);       
            }
        }
      }            

   }

   /*
   ** Basic constructor in the maze; blocks for wall or blocks for entry & exit locations
   ** block size: width * height
   **
   ** @param g2     The graphic context   
   **        x      The X coordinate of the upper-left corner of the Rectangle.
   **        y      The Y coordinate of the upper-left corner of the Rectangle.
   **        width  The width of the Rectangle
   **        height The height of the Rectangle
   **        color  Fill with color of the Rectangle
   */

   private void drawABrick(Graphics2D g2, int x, int y, int width, int height, Color color){
      g2.setColor(color);
      Rectangle rect = new Rectangle(x, y, width, height); 
      g2.fill(rect);
   }
   
}



