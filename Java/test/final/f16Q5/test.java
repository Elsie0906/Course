// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CS 455 PA4
// Fall 2017

import java.util.Scanner;
import java.io.*;
import java.util.*;

/**
 * input text: ahem. mary had a little lamb.
 * output text: ahem. lamb little a had mary.
 */

public class test {

  public static void main(String[] args)  {    
      
      Scanner in = new Scanner(System.in);

      System.out.println("please enter some words: ");

      printReversedSentences(in);

    
  }

  public static void printReversedSentences(Scanner in){

      Stack<String> st = new Stack<>();

      String line = in.nextLine();              // read a line
      Scanner lineScanner = new Scanner(line);

      while(lineScanner.hasNext()){           
        String word = lineScanner.next();
        st.push(word);

        if(word.charAt(word.length()-1) == '.')
          printOut(st);
      }

      System.out.println();
      //System.out.println("stack: " + st);


  }

  public static void printOut(Stack<String> stack){

      while(!stack.empty()){
          System.out.print(stack.pop() + " ");
      }
  }
}
