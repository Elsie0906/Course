// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// USC ID: 4337817705
// CS 455 PA4
// Fall 2017

/**
 * class RackTester (Unit Test Program)
 * 
 * 
 */

import java.util.LinkedList;
import java.util.ArrayList;

public class RackTester
{
   public static void main(String[] args)
   {
/*      
      Rack rack = new Rack();

      // create variables for the rack "aabbbd"
      String unique = "abd";   
      int[] mult = {2, 3, 1};

      ArrayList<String> sets = rack.getSubsets(unique, mult, 0);

      for(int i=0; i<sets.size(); i++){
         System.out.println(sets.get(i));
      }
*/
      String str = "aabbbd";
      Rack rack = new Rack();

      //rack.changeFormat();
      ArrayList<String> sets = rack.getSubsets(str);

      for(int i=0; i<sets.size(); i++){
         System.out.println(sets.get(i));
      }

   }

}