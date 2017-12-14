
/**
   Stores a sequence of integer data values and supports some computations
   with it.

   CS 455 Lab 4.
*/
import java.util.ArrayList;

public class Nums {

    private ArrayList<Integer> arrlist;
    /**
       Create an empty sequence of nums.
     */
    public Nums () {
      // create an empty array list
        arrlist = new ArrayList<Integer>();
    }

    /**
       Add a value to the end of the sequence.
     */
    public void add(int value) {
      // use add() method to add elements in the list
        arrlist.add(value);
    }


    /**
       Return the minimum value in the sequence.
       If the sequence is empty, returns Integer.MAX_VALUE
     */
    public int minVal() {

	return 0;    // stub code to get it to compile

    }

    /**
       Prints out the sequence of values as a space-separated list 
       on one line surrounded by parentheses.
       Does not print a newline.
       E.g., "(3 7 4 10 2 7)", for empty sequence: "()"
    */
    public void printVals() {
      // let us print all the elements available in list
        System.out.print("(");
        if(arrlist.size()>0){
          System.out.print(arrlist.get(0));
          for (int i=1; i<arrlist.size(); i++) {
            System.out.print(" " + arrlist.get(i));
          }
        }
        System.out.print(")");
    }

    /**
       Returns a new Nums object with all the values from this Nums
       object that are above the given threshold.  The values in the
       new object are in the same order as in this one.
       E.g.: call to myNums.valuesGT(10) where myNums = (3 7 19 4 21 19 10)
             returns      (19 21 19)
             myNums after call:  (3 7 19 4 21 19 10)
       The method does not modify the object the method is called on.
     */
    public Nums valuesGT(int threshold) {

	return new Nums();  // stub code to get it to compile

    }

    
}
