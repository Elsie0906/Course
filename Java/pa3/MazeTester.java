// Name: Yin-Hsia Yen
// USC NetID: 4337817705
// CS 455 PA1
// Fall 2017

/**
 * class MazeTester (Unit Test Program)
 * 
 * 
 */

import java.util.LinkedList;

public class MazeTester
{
   /* Test 1:
   private static final int[] input =    {0,1,1,1,1,1,1,1,1,1,
                                          0,1,1,0,0,0,0,0,0,0,
                                          0,0,0,0,1,1,1,1,1,0,
                                          1,0,1,1,1,0,1,0,1,0,
                                          1,0,0,0,0,0,1,0,0,0,
                                          1,0,1,1,0,1,1,1,0,1,
                                          1,0,1,0,0,0,1,1,1,1,
                                          0,0,0,0,1,0,0,0,0,0,
                                          1,1,1,1,1,1,1,1,1,0};

   private static final int NUMROW = 9;
   private static final int NUMCOL = 10;
   */

   // Test 2:
   private static final int[] input = {0,0,1,1,0,0,0,0,
                                       1,0,1,1,0,1,1,0,
                                       1,0,1,1,0,1,1,0,
                                       1,0,1,1,1,1,1,0,
                                       1,0,0,0,0,0,0,0};

   private static final int NUMROW = 5;
   private static final int NUMCOL = 8;

   public static void main(String[] args)
   {
      /*
      MazeCoord start = new MazeCoord(5,8);
      MazeCoord end = new MazeCoord(8,9);
      */
      MazeCoord start = new MazeCoord(2,4);
      MazeCoord end = new MazeCoord(0,0);

      boolean[][] mazeData = new boolean[NUMROW][NUMCOL];

      int count = 0;

      for(  int i=0; i<NUMROW; i++){
            for(  int j=0; j<NUMCOL; j++){
                  mazeData[i][j] = (input[count] == 1);
                  count++; 
            }
      }

      Maze maze = new Maze(mazeData, start, end);

      System.out.println("numRows: " + maze.numRows() + ", numCols: " + maze.numCols());
      System.out.println("start: " + maze.getEntryLoc().toString());
      System.out.println("end: " + maze.getExitLoc().toString());

      System.out.println("Initial maze: ");
      maze.disPlay();
      System.out.println();

      System.out.println("After search, find a path: " + maze.search());
      System.out.println();

      System.out.println("numRows: " + maze.numRows() + ", numCols: " + maze.numCols());
      System.out.println("start: " + maze.getEntryLoc().toString());
      System.out.println("end: " + maze.getExitLoc().toString());

      LinkedList<MazeCoord> path = maze.getPath();    

      int[][] finalPath = new int[NUMROW][NUMCOL];

/*
      for(  int i=0; i<NUMROW; i++){
            for(  int j=0; j<NUMCOL; j++){
                  if(mazeData[i][j] == true){
                     finalPath[i][j] = Maze.BARRRIER;
                  }
            }
      }
*/
      getMark(finalPath, path);  
      System.out.println("maze: ");
      displayFinal(finalPath);

   }
   private static void getMark(int[][] finalPath, LinkedList<MazeCoord> path){

      for(int i=0; i<path.size(); i++){
         MazeCoord node = path.get(i);
         //System.out.println("order: " + node.toString());
         int x = node.getRow();
         int y = node.getCol();

         finalPath[x][y] = Maze.VISITED;
      }

   } 

   private static void displayFinal(int[][] finalPath){

      for(  int i=0; i<NUMROW; i++){
            for(  int j=0; j<NUMCOL; j++){
                  System.out.print(finalPath[i][j] + " ");
            }
            System.out.println();
      }
   }

}