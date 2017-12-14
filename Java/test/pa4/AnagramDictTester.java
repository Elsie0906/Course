// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CS 455 PA4
// Fall 2017

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 */

public class AnagramDictTester {

  public static void main(String[] args){

    try{
        //String filename = "Anagramtest.txt";
        String filename = "sowpods.txt";

        AnagramDictionary dictionary = new AnagramDictionary(filename);

        //filename = new String("nums.txt");

        //dictionary = new AnagramDictionary(filename);   

        String str1 = "CALM";
        ArrayList<String> arrlist = dictionary.getAnagramsOf(str1);  

        // let us print all the elements available in list
        for (String element : arrlist) {
            System.out.println("element = " + element);
        }         

        String str2 = "rlee";

        ArrayList<String> list = dictionary.getAnagramsOf(str2);  

        // let us print all the elements available in list
        for (String element : list) {
            System.out.println("element = " + element);
        }        
    }

    catch (FileNotFoundException exception)
    {
        System.out.println("File not found: " + exception.getMessage());
    }

  }  
   
}
