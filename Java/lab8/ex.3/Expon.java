/*
** top-down recursive
*/
public class Expon {
    public static void main(String args[]) {
        
        int val = fastExpon(3,4);
        System.out.println("val: " + val);

        val = fastExpon(2,12);
        System.out.println("val: " + val);

    }
    public static int fastExpon(int x, int n) {

        if( n == 0)
            return 1;

        if( n == 1)
            return x;

        if( n % 2 == 0){
            n = n/2;
            int reVal = fastExpon(x, n);
            return reVal*reVal;
        }
        else{
            n = n/2;
            int reVal = fastExpon(x, n);
            return reVal*reVal*fastExpon(x,1);
        }


    }
}