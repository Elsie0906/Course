/* 
 * Contains printAlt method and tests it on a bunch of hardcoded test
 * cases, printing out test data and results for each one.
 */


import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

public class PrintAltTester {


    // prints alternate values in a list:
    // first, third, fifth, etc.
    public static void printAlt(LinkedList<Integer> list) {
        LinkedList<Integer> head = new LinkedList<>();

        //System.out.println("list: " + list);

        int size = list.size();

        for(int i=0; i<size; i++){
            //System.out.println("[DEBUG] list: " + list);
            if(i%2 == 0){
                head.add(list.pop());    
            }
            else
                list.remove();
        }

        System.out.println("head: " + head);
        //list = head;
    }



    public static void main(String args[]) {

    testPrintAlt("");
    testPrintAlt("3");
    testPrintAlt("3 6");
    testPrintAlt("3 2 4");
    testPrintAlt("3 2 4 5");
    testPrintAlt("3 2 4 5 2 2 9 4");
    testPrintAlt("3 2 4 5 2 2 9");

    }

    
    
    /*  Assumes the following format for list strings (first one
     *     is empty list):
     *   "", "3", "3 4", "3 4 5", etc.
     */
    public static LinkedList<Integer> makeList(String listString) {
    Scanner strscan = new Scanner(listString);

    LinkedList<Integer> list = new LinkedList<Integer>();

    while (strscan.hasNextInt()) {
        list.add(strscan.nextInt());
    }

    return list;
    }



    /* Test printAlt method on a list form of the input given in listString
     * Prints test data, and output of printAlt
     *
     * listString is a string form of a list given as a space separated
     * sequence of ints.  E.g.,
     *  "" (empty string), "3" (1 elmt string), "2 4" (2 elmt string), etc.
     *
     */
    public static void testPrintAlt(String listString) {

    LinkedList<Integer> list = makeList(listString);

    System.out.println("list: " + list);

    System.out.print("printAlt(list): ");
    printAlt(list);

    System.out.println();
    System.out.println();
    }
}