// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CS 455 PA4
// Fall 2017


/**
* ScoreTable class
* 
* Information about Scrabble scores for scrabble letters and words
* This class should work for both upper and lower case versions of the letters, e.g., 'a' and 'A' will have the same score
*/

public class ScoreTable {
   
  // table for Scrabble scores
  //                                      [A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q, R,S,T,U,V,W,X,Y,Z]
  private static final int[] SCORETABLE = {1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10};

/**
* look-up score from SCORETABLE
* @param str
*     string that we would like to know how much points it would have according to the score table
* @return 
*     total score of a word
**/

  public static int getScore(String str) {

      int idx = 0, score = 0, total = 0;

      String strLowerCase = str.toLowerCase();

      for(int i=0; i<strLowerCase.length(); i++){
          idx = strLowerCase.charAt(i) - 'a';
          score = SCORETABLE[idx];
          total = total + score;
      }

      return total;

  }  
   
}
