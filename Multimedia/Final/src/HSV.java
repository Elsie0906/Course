public class HSV{
    private int[][] arrayH;
    private int[][] arrayS;  // Pb is put here
    private int[][] arrayV;  // Pr is put here

    public HSV(int[][] h, int[][] s, int[][] v){
        this.arrayH = h;
        this.arrayS = s;
        this.arrayV = v;
    }
  
    public int[][] getH(){
        return(arrayH);
    }
  
    public int[][] getS(){
        return(arrayS);
    }
  
    public int[][] getV(){
        return(arrayV);
    }
}