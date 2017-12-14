import java.util.Scanner;

public class iterWordLen {
    public static int longestWordLen(String words) {
        int count = 0;
        Scanner scanner = new Scanner(words);

        while(scanner.hasNext()){
            String word = scanner.next();
            System.out.println("word: " + word);
            if(word.length() > count)
               count = word.length(); 
        }

        System.out.println("longest word length: " + count);
        return count;
    }

    public static void main(String args[]) {

        longestWordLen("What is the longest word");

    }
}