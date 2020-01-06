import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Temp {

	public static String prompt = ">";
	public static String delimeter = ";";

	public static int scoreLimit = 360;

	public static Map<String, String> lang_pack;

	public static String targetLanguageKey;
	public static String sourceLanguageKey;

	public static int pBarLength;



	public static void main(final String[] args) throws NoSuchAlgorithmException {

		final String dicPath = "dictionaries/";
		final String lesPath = "lessons/";
		final String usrPath = "users/";
		
		/**
		 * numberOfTuples:	number of word pairs to be read from the csv file for a lesson.
		 * wordsPerLesson:	number of Words per lesson (or part.) 
		 * 	!!! disclaimer: lessons and parts are not yet implemented differently.
		 * temporary buffer for reading the console input.
		 */
		final int numberOfTuples = 100;

		pBarLength = 20;


		// default language is turkish.
		lang_pack = LangPack.lang_Tr; 
		
		/**
		 * for console input
		 */
		final Scanner input = new Scanner(System.in);


		/**
		 * get username, if user exists, load data
		 * else create new user.
		 * TODO: used lang_Tr, handle?
		 */

		String username = readUsername(prompt);
		String password = readPassword(prompt);
		
		User loggedUser;
		if(User.userExists(username)){

			//password not yet fully implemented
			
			
			if(!CsvMisc.checkUserPassword(usrPath+username+".csv", password)){
				System.out.println(lang_pack.get("incorrect password"));
				System.exit(0);
			}
			
			loggedUser = CsvMisc.loadUserData(usrPath+username+".csv", username, password, delimeter);
			
			setSrcLang(loggedUser.getSourceLanguage());
			
			System.out.println(lang_pack.get("welcome")+username);

			sourceLanguageKey = loggedUser.getSourceLanguage();
			targetLanguageKey = loggedUser.getTargetLanguage();
			

		} else {
			loggedUser = new User(username, password);

			User.writeToUsersList(username);

			/**
			 * Prompt to choose source language
			 */
			System.out.println("Uygulama dilini seçiniz.\n[TR]\t[AZ]\t[KZ]");
			sourceLanguageKey = waitForAcceptedAnswer(new ArrayList<String>( Arrays.asList("tr", "az", "kz")));

			setSrcLang(sourceLanguageKey);

			/**
			 * Prompt to choose target language
			 */
			System.out.println("Öğrenmek istediğiniz dili seçiniz.\n[TR]\t[AZ]\t[KZ]");
			targetLanguageKey = waitForAcceptedAnswer(new ArrayList<String>( Arrays.asList("tr", "az", "kz")));

			loggedUser.addCourse(sourceLanguageKey + "-" + targetLanguageKey);
			CsvMisc.saveUserData(loggedUser, usrPath, delimeter);

		}

		String courseKey = sourceLanguageKey + "-" + targetLanguageKey;
		String csvName = courseKey + ".csv";
		String pathToLessonListCsv = lesPath + csvName;
		String pathToWordListCsv = dicPath + csvName;



		/**
		 * Optionally, we could have different dictionaries for different lessons.
		 * We could also have different dictionaries for different lesson parts but i will stick with the idea above and use an offset.
		 */
		final String[][] dictionary = CsvMisc.readDicFromCsv(pathToWordListCsv, numberOfTuples);

		/**
		 * 
		 */
		final ArrayList<String> lessons = CsvMisc.readCsvKeysToIAL(pathToLessonListCsv, delimeter);
		Map<String, int[]> lessonIndexes = CsvMisc.readCsvToLessonIndexMap(pathToLessonListCsv, delimeter);

		/**
		 * Main Page
		 */
		while(true){
			/**
			 * Print out the lesson layout.
			 *
			 * to do: lessons should have sublessons (namely parts.)    or should they ??
			 * These do not constitute a difference in implementation
			 * but a difference in respresentation.
			 */
			for(int i=0;i<lessons.size();i++){
				System.out.print(lang_pack.get("lesson") + lessons.get(i)+"\t");
				//neden 12? -> 360/(5*12) = 6. 5: full lesson credit. takes 6 sessions to fill up a lesson's bar.
				Misc.printProgressBar(loggedUser.getProgress(courseKey).getLessonTimesDone(lessons.get(i))*12, scoreLimit, 20);
				
			}
			System.out.println(lang_pack.get("choose lesson"));

			System.out.print(prompt);
			int selection = input.nextInt();

			if (selection==-1) break;

			Session sesh = new Session(loggedUser);
			sesh.startSession(dictionary, lessons.get(selection-1), lessonIndexes.get(lessons.get(selection-1))[0], lessonIndexes.get(lessons.get(selection-1))[1], scoreLimit);

			CsvMisc.saveUserData(loggedUser, usrPath, delimeter);

		}

		input.close();

	}


	public static int blockExercise(final String[] tuple){

		System.out.println(lang_pack.get("translate to " + targetLanguageKey));
		System.out.println(tuple[0]);

		ArrayList<Pair<String, Integer>> pinboard = new ArrayList<Pair<String, Integer>>();
		ArrayList<Pair<String, Integer>> userDeck = new ArrayList<Pair<String, Integer>>();


		String[] tokens = tuple[1].split(" |\'");

		for(int i=0;i<tokens.length;i++){
			Pair<String, Integer> pair = Pair.createPair(tokens[i], i);
			userDeck.add(pair);
		}

		Collections.shuffle(userDeck);

		for(int i=0;i<userDeck.size();i++){
			System.out.print("["+(i+1)+": "+userDeck.get(i).getValue0()+"]");
		}
		System.out.println("");

		final Scanner input = new Scanner(System.in);
		System.out.print(prompt);

		//no bugcheck
		while(true){

			boolean isComplete = false;
			String in = input.next();
			int answer;
			
			//TODO: burası çok sorunlu ama iş görüyor for test purposes
			while(true){
				try {
					in = input.next();
					answer = Integer.parseInt(in);
					break;
				} catch (final NumberFormatException e){
					System.out.println(lang_pack.get("enter an integer value"));
					continue;
				}
			}
			
			

			if(answer>0){
				pinboard.add(userDeck.get(answer-1));

			} else if(answer==-1){
				pinboard.remove(pinboard.size()-1);
			} 

			for(int i=0;i<pinboard.size();i++){
				System.out.print(pinboard.get(i).getValue0() + " ");
			}
			System.out.println();
			
			if(answer==0){
				isComplete = true;
				for(int i=0;i<pinboard.size();i++){
					if(pinboard.get(i).getValue1()!=i) isComplete = false;
				}
				if(isComplete && pinboard.size()!=0){
					System.out.println(lang_pack.get("right answer") +"\n");
					input.close();
					return 1;
				} else {
					System.out.println(lang_pack.get("wrong answer") + tuple[1] +"\n");
					input.close();
					return 0;
				}
			}
			
		}

		
	}


	/**
	 * read input from console alas input not accepted
	 * TODO: can't close scanner without exception?
	 * 
	 * @param acceptedAnswers : a list of accepted input strings
	 * @return
	 */
	public static String waitForAcceptedAnswer(final List<String> acceptedAnswers) {

		String inputBuffer = "";
		while (!(acceptedAnswers.contains(inputBuffer) || acceptedAnswers.contains(inputBuffer.toLowerCase()))) {
			final Scanner input = new Scanner(System.in);
			System.out.println(prompt);
			inputBuffer = input.nextLine();
		}
		return inputBuffer;
	}

	public static void setSrcLang(String src){
		if(src.equals("tr")){
			lang_pack = LangPack.lang_Tr;
		} else if (src.equals("az")) {
			lang_pack = LangPack.lang_Az;
		} else if (src.equals("kz")) {
			lang_pack = LangPack.lang_Kz;
		}
	}

	private static String readUsername(String prompt){

		Scanner input = new Scanner(System.in);
		System.out.println(lang_pack.get("enter username"));
		System.out.print(prompt);
		return input.next();

	}

	private static String readPassword(String prompt){
		System.out.println(lang_pack.get("enter password"));  
		return maskedReader(prompt);
	}

    public static String maskedReader(String prompt) {    
		  
        Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }

        String passwordArray = new String(console.readPassword(prompt));

		return passwordArray;
    }

}