/**
   This class tests the Term class w/o assertion.
   CS 455  Lab 5.
*/
public class AssertTester
{
    public static void main(String[] args){
        
        double coeff = 2.0;
        int expon = 3;

        System.out.println("initial configuration(no input value)");

        Term initial = new Term();

        initial.toString();

        System.out.println("new configuration with coeff " + coeff + " expon " + expon);

        Term term1 = new Term(coeff,expon);

        System.out.println("check the value: coeff" + term1.getCoeff() + " expon " + term1.getExpon());

        expon = -1;

        System.out.println("Try to make an assertion: coeff " + coeff + " expon " + expon);

        Term term2 = new Term(coeff, expon);

        term2.toString();
    }
}