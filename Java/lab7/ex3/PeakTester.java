/* 
 * Contains hasPeak method and
 * tests it on a bunch of hardcoded test cases, printing out test
 * data, actual results, and a FAILED message if actual results don't
 * match expected results.
 */

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

public class PeakTester {


    /*
     * see lab assgt for specification of hasPeak method.
     */
    public static boolean hasPeak(LinkedList<Integer> list) {

        if( list.size() <= 2)
            return false;

        ListIterator<Integer> iter = list.listIterator();

        int preVal = 0, val = 0;

        boolean up = false, down = false;
        int countUp = 0, countDown = 0;

        if( iter.hasNext()){
            preVal = iter.next();
        }        

        while(iter.hasNext()){
            val = iter.next();

            //System.out.println("[DEBUG] preVal: " + preVal + " val: " + val);

            if( val > preVal && up == false && down == false){
                //System.out.println("[DEBUG] case1");
                countUp++;
                up = true;
            }else if(val > preVal && up == false && down == true){
                //System.out.println("[DEBUG] case2");
                down = false;
                up = true;
                countUp++;
            }else if(val < preVal && up == false && down == false){
                //System.out.println("[DEBUG] case3");
                countDown++;
                down = true;
            }else if(val < preVal && up == true && down == false){
                //System.out.println("[DEBUG] case4");
                countDown++;
                up = false;
                down = true;
            }

            preVal = val;
        }

        //System.out.println("[DEBUG] up: " + countUp + " down: " + countDown);

        int peak = (int) Math.max(countUp, countDown);

        //System.out.println("[DEBUG] peak: " + peak);
        if( peak != 1)
            return false;

        if( countUp == 0 || countDown == 0)
            return false;

        if( peak == 1 && up == true)
            return false;

/*      WRONG ANS!!
        ListIterator<Integer> iter = list.listIterator();
        int val1 = 0, val2 = 0, val3 = 0;
        if( iter.hasNext()){
            val1 = iter.next();
        }

        if( iter.hasNext()){
            val2 = iter.next();
        }

        int count = 0;

        while(iter.hasNext()){
            val3 = iter.next();

            System.out.println("[DEBUG] val1: " + val1 + " val2: " + val2 + " val3: " + val3);
            if(val2 > val1 && val2>val3)
                count++;

            val1 = val2;
            val2 = val3;
        }
        System.out.println("count: " + count);
        if(count != 1)
            return false;
*/
        return true;  // DUMMY CODE TO GET IT TO COMPILE

    }



    public static void main(String args[]) {

    testPeak("", false);
    testPeak("3", false);
    testPeak("3 4", false);
    testPeak("4 2", false);
    testPeak("3 4 5", false);
    testPeak("5 4 3", false);
    testPeak("3 4 5 3", true);
    testPeak("3 4 5 3 2", true);
    testPeak("3 7 9 11 8 4 3 1", true);
    testPeak("3 5 4", true);
    testPeak("4 3 5", false);
    testPeak("2 4 3 5", false);
    testPeak("5 2 4 3 5", false);
    testPeak("5 2 4 3", false);
    testPeak("2 4 3 5 2", false);  // 2 peaks
    }

    
    
    /*  Assumes the following format for list strings (first one
     *     is empty list):
     *   "", "3", "3 4", "3 4 5", etc.
     */
    public static LinkedList<Integer> makeList(String listString) {
    Scanner strscan = new Scanner(listString);

    LinkedList<Integer> list = new LinkedList<Integer>();

    // strscan.skip("\\[");  // consume the "["
    while (strscan.hasNextInt()) {
        list.add(strscan.nextInt());
    }

    return list;
    }


    /* Test hasPeak method on a list form of input given in listString
     * Prints test data, result, and whether result matched expectedResult
     *
     * listString is a string form of a list given as a space separated
     * sequence of ints.  E.g.,
     *  "" (empty string), "3" (1 elmt string), "2 4" (2 elmt string), etc.
     *
     */
    public static void testPeak(String listString, boolean expectedResult) {

    LinkedList<Integer> list = makeList(listString);

    boolean result = hasPeak(list);
    System.out.print("List: " + list
               + " hasPeak? " + result);
    if (result != expectedResult) {
        System.out.print("...hasPeak FAILED");
    }
    System.out.println();
    }
}