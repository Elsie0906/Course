import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.Set;
import java.util.Iterator;
import java.util.Comparator;
import java.util.*;


/**
 * Computes the number of occurrences of all words from a Scanner.
 * Strips a word of surrounding punctuation and converts to all lower
 * case before counting it.
 * Allows for output in map-order (print) or sorted in decreasing order
 * by number of occurrences (printSorted)
 * or output of only entries satisfying some condition (printSatisfying)
 *
 * Version for lab.
 */
public class Concord {
	
    private Map<String, Integer> concord;

    // inline class
    private static class DescendingComparator implements Comparator<Map.Entry<String, Integer>>{
        public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
            Integer val1 = entry1.getValue();
            Integer val2 = entry2.getValue();

            if( val1 > val2){
                return -1;
            }else if(val1 < val2){
                return 1;
            }else
                return 0;
        }
    }
	
    /**
     * Creates empty concordance
     */
    public Concord() {
	   concord = new TreeMap<String, Integer>();
    }
	
    /**
     * Add data from Scanner to concordance.
     * @param in data to scan.  "in" will be at the end of its data after this
     * operation.
     */
    public void addData(Scanner in) {
	   while (in.hasNext()) {
			
	       String word = in.next();
	       word = filter(word);  // remove punctuation, convert upper case
	       Integer oldValue = concord.get(word);
			
	       if (oldValue == null) {
		      concord.put(word, 1);
	       }
	       else {
		      concord.put(word, oldValue + 1);
	       }
	   }
		
    }
	
    public String toString() {
	   return concord.toString();
    }
	
    /**
     * Write concordance data to out.
     * Format is one entry per line: word number
     * where "number" is the number of occurrrences of that word.
     * @param out where to write the results.
     */
    public void print(PrintStream out) { 

	   // version with for each loop

       out.println("Concord alpha order:"); 

	   for (Map.Entry<String, Integer> curr : concord.entrySet()) {
	       out.println(curr.getKey() + " " + curr.getValue());
	   }

    }
	

    /**
     * Write concordance data to "out" in decreasing order by 
     * number of occurrences.
     * Format is one entry per line: word number
     * where "number" is the number of occurrrences of that word.
     * @param out where to write the results.
     */
    public void printSorted(PrintStream out) {

        // save all the entry into an arraylist
        ArrayList<Map.Entry<String, Integer>> arrlist = new ArrayList<>();

        arrlist.addAll(concord.entrySet());

        //[DEBUG]
        //out.println(arrlist);

        Collections.sort(arrlist, new DescendingComparator());

        //out.println(arrlist);

        out.println("Concord numeric order:"); 

        for (Map.Entry<String, Integer> curr : arrlist) {
           out.println(curr.getKey() + " " + curr.getValue());
        }

    }
	
    // NOTE: printSatisfying only used in Ex. 3
    /**
     * Writes some entries to out, using same format as Concord print method
     * The entries it writes are the the ones that satisfying pred.
     * @param out the outstream to write to
     * @param pred the predicate that each entry is tested on.
     */
    public void printSatisfying(PrintStream out, Predicate pred) {
	for (Map.Entry<String,Integer> entry : concord.entrySet()) {

	    if (pred.predicate(entry)) {
		out.println(entry.getKey() 
			       + " " + entry.getValue());
	    }

	}
    }

    /**
     * Returns a version of the word that's all lower case
     * with leading and trailing punctuation removed.
     * (Keeps internal punctuation, such as "won't")
     * @param word the word to filter
     * @return the filtered word
     */
    public static String filter(String word) {
	// match beginning of string followed one or more non-word chars OR
	// match one or more non-word chars followed by end of string
	// replaces such sequences with the empty string
	String newWord = word.replaceAll("(\\A[^\\w]+)|([^\\w]+\\z)", "");
	   newWord = newWord.toLowerCase();
	   return newWord;
    }

}

