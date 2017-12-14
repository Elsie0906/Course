import java.util.Scanner;
import java.util.*;

public class recursiveWordLen {
    public static void main(String args[]) {

        int count = longestWordLen("What is the longest word");
        System.out.println("word len: " + count);

        count = longestWordLen("Some Words");
        System.out.println("word len: " + count);

        /* test

        String str = "I am Elsie";
        Scanner test = new Scanner(str);
        String str2 = "I";

        test.skip(str2);
        System.out.println(str);

        */


    }

    public static int longestWordLen(String words) {

        return longestWordLen(words, 0, words.length());
    }

    public static int longestWordLen(String words, int start, int end){

        if(start >= end)
            return 0;

        String sub = words.substring(start,end);

        //System.out.println("[DEBUG] substring: " + sub);
        Scanner scanner = new Scanner(sub);

        String word = "";
        int idx = 0;
        if( scanner.hasNext()){
            word = scanner.next();
            idx = sub.indexOf(word);
            //System.out.println("word: " + word + " len: " + word.length()+ " idx: " + idx);
        }
        return (int)Math.max(word.length(), longestWordLen(sub, idx+word.length(), sub.length())); 
    }

}