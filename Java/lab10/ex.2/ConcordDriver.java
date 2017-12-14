import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/**
 * Finds frequency of each word in a file.
 * Unlike the lecture version of this code, this one is smarter
 * about what it considers a word.
 *
 * Version for the lab.
 */

public class ConcordDriver {


    public static void main(String[] args) {

	  	String fileName = "";
	  	Scanner in = new Scanner(System.in);

      	try {

        	 if (args.length < 1) {
            	System.out.println("ERROR: missing file name command line argument");
         	}
         	else {
            	fileName = args[0];
            	File inFile = new File(fileName);    
            	in = new Scanner(inFile);	
        	}

      	}
      	catch (FileNotFoundException exc) {
         	System.out.println("ERROR: File not found: " + fileName);
      	}
		
		Concord concord = new Concord();
		
		concord.addData(in);		
		
    	concord.print(System.out);

     	concord.printSorted(System.out);
		
    }

}
