/*
** bottom-up recursive
*/
public class recursiveString {
    public static void main(String args[]) {
        String ss = string1UpToN(3);
        System.out.println("val: " + ss);

    }
    public static String string1UpToN(int n)  {
        return RAppendUpTo("", 1, n); 
    }
    private static String RAppendUpTo(String strSoFar, int i, int n){

    	strSoFar = strSoFar + i;

    	System.out.println("[DEBUG] string:" + strSoFar);

    	if( i==n)
    		return strSoFar;
    	else{
    		i++;
    		strSoFar = strSoFar + " ";
    	    return RAppendUpTo(strSoFar, i, n);    		
    	}

    }
}