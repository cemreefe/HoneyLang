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
			sourceLanguageKey = waitForAcceptedStringAnswer(new ArrayList<String>( Arrays.asList("tr", "az", "kz")));

			setSrcLang(sourceLanguageKey);

			/**
			 * Prompt to choose target language
			 */
			System.out.println("Öğrenmek istediğiniz dili seçiniz.\n[TR]\t[AZ]\t[KZ]");
			targetLanguageKey = waitForAcceptedStringAnswer(new ArrayList<String>( Arrays.asList("tr", "az", "kz")));

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
			System.out.println(lang_pack.get("choose lesson")+"(-1: exit)");
			
			int selection = waitForAcceptedIntegerAnswer(Misc.rangeIAL(1, lessons.size()+1));

			if(selection==-1){
				break;
			}

			Session sesh = new Session(loggedUser);
			sesh.startSession(dictionary, lessons.get(selection-1), lessonIndexes.get(lessons.get(selection-1))[0], lessonIndexes.get(lessons.get(selection-1))[1], scoreLimit);

			CsvMisc.saveUserData(loggedUser, usrPath, delimeter);

		}

		input.close();

	}


	/**
	 * read input from console alas input not accepted
	 * TODO: can't close scanner without exception?
	 * 
	 * @param acceptedAnswers : a list of accepted input strings
	 * @return
	 */
	public static String waitForAcceptedStringAnswer(final List<String> acceptedAnswers) {
		
		final Scanner input = new Scanner(System.in);
		System.out.println(prompt);
		String inputBuffer = input.nextLine();

		while (!(acceptedAnswers.contains(inputBuffer) || acceptedAnswers.contains(inputBuffer.toLowerCase()))) {
			System.out.println("Accepted answers: " + acceptedAnswers); //TODO:
			System.out.println(prompt);
			inputBuffer = input.nextLine();
		}
		return inputBuffer;
	}

	public static int waitForAcceptedIntegerAnswer(final List<Integer> acceptedAnswers) {

		final Scanner input = new Scanner(System.in);
		System.out.println(prompt);
		int inputBuffer = input.nextInt();

		while (!(acceptedAnswers.contains(inputBuffer))) {
			System.out.println("Accepted answers: " + acceptedAnswers); //TODO:
			System.out.println(prompt);
			inputBuffer = input.nextInt();
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