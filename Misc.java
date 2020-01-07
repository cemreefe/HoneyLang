import java.util.*;

class Misc {

    public static ArrayList<Integer> rangeIAL(int start, int exclude){
        ArrayList<Integer> l = new ArrayList<Integer>();
        for(int i=start;i<exclude;i++){
            l.add(i);
        }
        return l;
    }

    public static ArrayList<String> rangeSAL(int start, int exclude){
        ArrayList<String> l = new ArrayList<String>();
        for(int i=start;i<exclude;i++){
            l.add(""+i);
        }
        return l;
    }

    public static int[] stoia(String s) {
		String[] str = s.split(",");
		int size = str.length;
		int [] arr = new int [size];
		for(int i=0; i<size; i++) {
		   arr[i] = Integer.parseInt(str[i]);
		}
		return arr;
	}

	public static String iatos(int[] a) {
		int size = a.length;
		String s = "";
		for(int i=0; i<size; i++) {
		  s+=a[i]+",";
		}
		return s.substring(0,s.length()-1);
	}
	

	

	public static void printProgressBar(int score, int scoreLimit, int length){
		System.out.print(ANSI_BLUE);
		System.out.print("");
		for(int i=0;i<length;i++){
			if(i<score/(scoreLimit/length)) System.out.print("▮");
			else System.out.print("▯");
		}
		System.out.println(ANSI_RESET);
		System.out.println("");
    }
    
    public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

}