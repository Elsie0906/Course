/**
   This class Using an Input Reader to get info and store the value into an ArrayList.
   CS 455  Lab 5.
*/
import java.util.ArrayList;
import java.util.Scanner;

public class ReadTester
{
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);

        ArrayList<Integer> list = new ArrayList<>();
        
        System.out.print("Enter a space separated list of numbers: "); 

        while (in.hasNextLine()){    
            
            String line = in.nextLine();

            // convert string to number
            int num = -123456, val = 0;

            for(int i=0; i<line.length(); i++){
                if(!Character.isDigit(line.charAt(i))){
                  if(num != -123456){
                    list.add(val);
                  }
                  num = -123456;
                  val = 0;
                  continue;
                }

                //System.out.println("start idx: " + i + " digit: " + line.charAt(i));

                num = line.charAt(i) - '0';
                val = val*10 + num;

                //System.out.println("digit: " + num + " val: " + val);

                if( i == line.length()-1)
                    list.add(val);
            }

            System.out.println("The numbers were: " + list); 
            list.clear();
            System.out.print("Enter a space separated list of numbers: ");
        }
    }
}