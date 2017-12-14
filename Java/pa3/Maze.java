// Name: Yin-Hsia Yen
// USC loginid: yinhsiay 
// CS 455 PA3
// Fall 2017

import java.util.LinkedList;


/**
   Maze class

   Stores information about a maze and can find a path through the maze
   (if there is one).
   
   Assumptions about structure of the maze, as given in mazeData, startLoc, and endLoc
   (parameters to constructor), and the path:
     -- no outer walls given in mazeData -- search assumes there is a virtual 
        border around the maze (i.e., the maze path can't go outside of the maze
        boundaries)
     -- start location for a path is maze coordinate startLoc
     -- exit location is maze coordinate exitLoc
     -- mazeData input is a 2D array of booleans, where true means there is a wall
        at that location, and false means there isn't (see public FREE / WALL 
        constants below) 
     -- in mazeData the first index indicates the row. e.g., mazeData[row][col]
     -- only travel in 4 compass directions (no diagonal paths)
     -- can't travel through walls

 */

public class Maze {
   
   public static final boolean FREE = false;
   public static final boolean WALL = true;

   public static final int FREESPACE = 0;    // If there is no wall and this loc has not been visited, mark it as "FREESPACE"
   public static final int BARRRIER = 1;     // If there is a wall, mark it as "BARRIER" in the 2D array mazeMark
   public static final int VISITED = 2;      // If this location has been visited, mark it as "VISITED" in the 2D array mazeMark

   /*
   ** Instance variable:
   **              start      entry point of the maze
   **              end        exit point of the maze
   **              mazeMark   using an 2D integer array to record "Free", "Wall" or "Visited" 
   **              path       a linked list; save a path after search
   **              findPath   result for search(); return the result if a path already found 
   */

   private MazeCoord start;
   private MazeCoord end;

   private int[][] mazeMark;

   private LinkedList<MazeCoord> path; 

   private boolean findPath = false;
   
  

   /**
      Constructs a maze.
      @param mazeData the maze to search.  See general Maze comments above for what goes in this array.
      @param startLoc the location in maze to start the search (not necessarily on an edge)
      @param exitLoc the "exit" location of the maze (not necessarily on an edge)
      PRE: 0 <= startLoc.getRow() < mazeData.length and 0 <= startLoc.getCol() < mazeData[0].length
         and 0 <= endLoc.getRow() < mazeData.length and 0 <= endLoc.getCol() < mazeData[0].length

      1. Turn mark in mazeData to mazeMark cause we only use an array to represent loc is "Free", "Wall" or "Visited"
      2. Save the entry & exit loc of the maze
      3. Create a linked list "path" for search() 
    */
   public Maze(boolean[][] mazeData, MazeCoord startLoc, MazeCoord exitLoc) {

        int m = mazeData.length;
        int n = mazeData[0].length;

        // Initialize
        mazeMark = new int[m][n];

        // if there is a wall, mark it as "BARRRIER"

        for(int i=0; i<m; i++){
            for(int j=0; j<n; j++){
              if( mazeData[i][j] == WALL){
                  mazeMark[i][j] = BARRRIER;
              }
            }
        }

        // save the location of start & end

        start = new MazeCoord(startLoc.getRow(),startLoc.getCol());
        end = new MazeCoord(exitLoc.getRow(),exitLoc.getCol());

        // final path

        path = new LinkedList<MazeCoord>();
 
   }


   /**
      Returns the number of rows in the maze
      @return number of rows
   */
   public int numRows() {
      return mazeMark.length;   
   }

   
   /**
      Returns the number of columns in the maze
      @return number of columns
   */   
   public int numCols() {
      return mazeMark[0].length;  
   } 
 
   
   /**
      Returns true iff there is a wall at this location
      @param loc the location in maze coordinates
      @return whether there is a wall here
      PRE: 0 <= loc.getRow() < numRows() and 0 <= loc.getCol() < numCols()

      Check the label in array mazeMark if it is marked as "BARRRIER"
   */
   public boolean hasWallAt(MazeCoord loc) {

      int x = loc.getRow();
      int y = loc.getCol();

      if( mazeMark[x][y] == BARRRIER)
          return true;

      return false;   
   }
   

   /**
      Returns the entry location of this maze.
    */
   public MazeCoord getEntryLoc() {

      return start;   
   }
   
   
   /**
     Returns the exit location of this maze.
   */
   public MazeCoord getExitLoc() {

      return end;   
   }

   
   /**
      Returns the path through the maze. First element is start location, and
      last element is exit location.  If there was not path, or if this is called
      before a call to search, returns empty list.

      @return the maze path
    */
   public LinkedList<MazeCoord> getPath() {

      return path;

   }


   /**
      Find a path from start location to the exit location (see Maze
      constructor parameters, startLoc and exitLoc) if there is one.
      Client can access the path found via getPath method.

      @return whether a path was found.
    */
   public boolean search()  {  

      MazeCoord current = start;

      if( helper(current) == true){
          findPath = true;
      }

      return findPath;

   }

   /**
   *** Recursive search
   *** 1. Base cases:
   ***    if the loc(r,c) is out of boundary, return failure
   ***    if (r,c) is a wall, return failure
   ***    if (r,c) has already been visited, return failure
   ***    if (r,c) is the final position, mark (r,c) a part of the path and return success
   *** 2. recursive cases:
   ***    for each position mark (r,c) a part of the path and then try the adjacent position (r', c')
   ***    only four compass directions: up, down, left, right
   ***    remove (r,c) if there is no way out of the maze (four compass directions all return failure)
   ***
   *** Traverse: DFS
   *** 
   *** PRE: 0 <= cur.getRow() < numsRows()
   ***      0 <= cur.getCol() < numsCols()
   ***
   *** @param cur: current searching location in the maze 
   **/

   private boolean helper(MazeCoord cur){

      // base case:

      int x = cur.getRow();
      int y = cur.getCol();

      // out of boundary, return false
      if( x < 0 || x >= this.numRows() || y < 0 || y >= this.numCols())
          return false;

      // 1. It there is a wall, return false
      if( hasWallAt(cur) == true)
          return false;

      // 2. If this loc has been visited, return false
      if( mazeMark[x][y] == VISITED)
          return false;
          
      // 3. If reached endLoc, return true
      if( cur.equals(this.end) == true){
          path.add(cur);
          return true;
      }
      // algorithm: DFS 
      // recursive: four directions up, down, right & left
      // using linked list as a stack(LIFO)

      // mark current loc as VISITED, and add it as a part of the path
      if( mazeMark[x][y] == FREESPACE){
          mazeMark[x][y] = VISITED;
          path.add(cur);
          //System.out.println(cur.toString());
      }

      if( helper(new MazeCoord(x-1, y)) == true)  // direction: up
          return true;

      if( helper(new MazeCoord(x+1, y)) == true)  // keep trying, direction: down
          return true;

      if( helper(new MazeCoord(x, y-1)) == true)  // keep trying, direction: left
          return true;

      if( helper(new MazeCoord(x, y+1)) == true)  // keep trying, direction: right
          return true;

      path.removeLast();                          // no way out of the maze through this loc, remove it
      return false;         
     
   }

   /*
   ** [Debugging] Show all elements in array mazeMark
   */ 
   public void disPlay(){
      int m = numRows();
      int n = numCols();

      System.out.println();

      for(int i=0; i<m; i++){
         for(int j=0; j<n; j++){
            System.out.print(mazeMark[i][j] + " ");
         }
         System.out.println();
      }
   }

}
