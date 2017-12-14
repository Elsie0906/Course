// Name: Yin-Hsia Yen
// USC loginid: yinhsiay 
// CS 455 PA3
// Fall 2017


import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFrame;
import java.util.Scanner;
import java.io.File;


/**
 * MazeViewer class
 * 
 * Program to read in and display a maze and a path through the maze. At user
 * command displays a path through the maze if there is one.
 * 
 * How to call it from the command line:
 * 
 *      java MazeViewer mazeFile
 * 
 * where mazeFile is a text file of the maze. The format is the number of rows
 * and number of columns, followed by one line per row, followed by the start location, 
 * and ending with the exit location. Each maze location is
 * either a wall (1) or free (0). Here is an example of contents of a file for
 * a 3x4 maze, with start location as the top left, and exit location as the bottom right
 * (we count locations from 0, similar to Java arrays):
 * 
 * 3 4 
 * 0111
 * 0000
 * 1110
 * 0 0
 * 2 3
 * 
 */

public class MazeViewer {
   
   private static final char WALL_CHAR = '1';
   private static final char FREE_CHAR = '0';

   public static void main(String[] args)  {

      String fileName = "";

      try {

         if (args.length < 1) {
            System.out.println("ERROR: missing file name command line argument");
         }
         else {
            fileName = args[0];
            
            JFrame frame = readMazeFile(fileName);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setVisible(true);
         }

      }
      catch (FileNotFoundException exc) {
         System.out.println("ERROR: File not found: " + fileName);
      }
      catch (IOException exc) {
         exc.printStackTrace();
      }
   }

   /**
    readMazeFile reads in maze from the file whose name is given and returns a MazeFrame created from it.
   
   @param fileName
             the name of a file to read from (file format shown in class comments, above)
   @returns a MazeFrame containing the data from the file.
        
   @throws FileNotFoundException
              if there's no such file (subclass of IOException)
   @throws IOException
              (hook given in case you want to do more error-checking --
               that would also involve changing main to catch other exceptions)
   */
   private static MazeFrame readMazeFile(String fileName) throws IOException {

      File inFile = new File(fileName);

      // if file is not found, throws FileNotFoundExcpetion
      Scanner in = new Scanner(inFile); 

      // intial status of the maze and save it into boolean array mazeData
      boolean[][] mazeData = readMazeData(in);
      
      // entry loc of the maze
      int x = readNextInt(in);
      int y = readNextInt(in);     

      MazeCoord start = new MazeCoord(x,y);

      //System.out.println("start: " + start.toString());

      // exit loc of the maze
      x = readNextInt(in);
      y = readNextInt(in);
      MazeCoord end = new MazeCoord(x,y);

      //System.out.println("end: " + end.toString());

      return new MazeFrame(mazeData, start, end);

   }
   /*
   **  Read data for array mazeData
   **  @param  in          Scanner input reader
   **  @throws IOException if there's no description for size of array mazeData
   **                      if there's no data for array mazeData 
   **  @return mazeData    2D boolean array
   */
   private static boolean[][] readMazeData(Scanner in) throws IOException{

      int numberOfRows = readNextInt(in);
      int numberOfCols = readNextInt(in);

      //System.out.println("[DEBUG] numberOfRows: " + numberOfRows + ", numberOfCols: " + numberOfCols);

      boolean[][] mazeData = new boolean[numberOfRows][numberOfCols];

      String s = "";

      for(int i=0; i<numberOfRows; i++){
          if( !in.hasNext()){
              throw new IOException("ERROR: bad data");
          }

          s = in.next();

          for(int j=0; j<numberOfCols; j++){
              mazeData[i][j] = (s.charAt(j) == WALL_CHAR); 
          }                
      }
      return mazeData;
   }

   /*
   **  Read next integer
   **  @param  in          Scanner input reader
   **  @throws IOException if there's no integer 
   */
   private static int readNextInt(Scanner in) throws IOException{
      if (!in.hasNextInt()) 
      {
         throw new IOException("ERROR: bad data");
      }

      return in.nextInt();      
   }

}
