import java.io.*;  
import java.util.*; 



public class Temp {

	public static String translate_this_to = "Bu cümleyi Azerbaycan Türkçesi'ne çeviriniz.";

	public static String prompt = ">";

	public static int scoreLimit = 360;
	

	public static void main(String[] args) {

		//translate_this_to = "Bu cümləni türk dilinə tərcümə edin.";

		String pathToCsv = "dictionaries/tr-az.csv";
		
		int numberOfTuples = 100;
		int wordsPerLesson = 10;
		
		String[][] dictionary = readFromCsv(pathToCsv, numberOfTuples);

		for(int i=0;i<10;i++){
			System.out.println("Ders " + (i+1));
		}
		System.out.println("^Listeden bir ders seçiniz. (Sadece dersin numarasını giriniz.)");
		
		Scanner input = new Scanner(System.in);
		System.out.print(prompt);
		int selection = input.nextInt();
		
		writeOutSession(wordsPerLesson, dictionary, (selection-1)*wordsPerLesson);

	}

	public static int writeOutSession(int numberOfWords, String[][] dictionary, int offset){
		//pick a number of words from dictionary

		ArrayList<String[]> wordList = new ArrayList<String[]>(); 

		for(int i=0;i<numberOfWords;i++){
			wordList.add(dictionary[offset + i]);
		}

		Random random = new Random();
		int score=0; 
		int i;
		int c = numberOfWords;
		//add multiplier
		while(score<360){
			printProgressBar(score, 20);
			i = random.nextInt(c);
			String[] tuple = wordList.get(i);
			int exerciseScore = writeOutExercise(tuple);
			if(exerciseScore==1){
				wordList.remove(i);
				c--;
				score+=exerciseScore*(scoreLimit/numberOfWords);
			}
			//turn into function

		}
		printProgressBar(score, 20);
		System.out.println("Session complete, good job!");
		return score;
	}
	public static int writeOutExercise(String[] tuple){

		System.out.println(translate_this_to);
		System.out.println(tuple[0]);

		Scanner input = new Scanner(System.in);
		System.out.print(prompt);
		String answer = input.nextLine();
		if(answer.equals(tuple[1]) || answer.equals(tuple[1].replaceAll("ə", "é"))){
			System.out.println("Tebrikler, doğru cevap!\n");
			return 1;
		} else {
			System.out.println("Yanlış cevap, doğrusu: " + tuple[1] +"\n");
			return 0;
		}
		
	}

	public static String[][] readFromCsv(String pathToCsv, int numberOfTuples){
		try {
			BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));

			String[][] dictionary = new String[numberOfTuples][2];
			String row; int i=0;
			while (i<numberOfTuples+1 && (row = csvReader.readLine()) != null) {

				if(i==0) {i++; continue;}

				String[] data = row.split(";");
				//System.out.println(i);
				dictionary[i-1][0] = data [0];
				dictionary[i-1][1] = data [1];
 				i++;
				
			}
			csvReader.close();
			return dictionary;


		}
		catch (FileNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e){
			System.out.println(e);
		}

		return null;
	}

	//TODO: change delimeter to ";"
	public static void writeToCsv(String pathToCsv, String s){

		try {
			FileWriter csvWriter = new FileWriter(pathToCsv);
			csvWriter.append(s);
			csvWriter.flush();
			csvWriter.close();

		}
		catch (FileNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e){
			System.out.println(e);
		}
	}

	public static void printProgressBar(int score, int length){
		System.out.print("[");
		for(int i=0;i<length;i++){
			if(i<score/(scoreLimit/length)) System.out.print("█");
			else System.out.print(" ");
		}
		System.out.println("]");
	}

}


