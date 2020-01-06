import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * user data is independent of language!!!!
 */

public class Temp {

	public static String prompt = ">";
	public static String delimeter = ";";

	public static int scoreLimit = 360;

	public static Map<String, String> lang_pack;

	public static String targetLanguageKey;
	public static String sourceLanguageKey;

	public static int pBarLength;

	/**
	 * read input from console alas input not accepted
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
			lang_pack = lang_Tr;
		} else if (src.equals("az")) {
			lang_pack = lang_Az;
		} else if (src.equals("kz")) {
			lang_pack = lang_Kz;
		}
	}

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

		/**
		 * USER FIELDS
		 */

		String courseKey;
		String csvName;
		String pathToLessonListCsv;
		String pathToWordListCsv;


		// default language is turkish.
		lang_pack = lang_Tr; 
		
		/**
		 * for console input
		 */
		final Scanner input = new Scanner(System.in);


		/**
		 * get username, if user exists, load data
		 * else create new user.
		 * TODO: used lang_Tr, handle?
		 */
		System.out.println(lang_pack.get("enter username"));
		System.out.println(prompt);
		String username = input.next();
		String password = "";
		
		User loggedUser;
		if(User.userExists(username)){

			//password not yet fully implemented
			/*
			while(loggedUser.getName().equals("!wrongpassword")){

			}
			*/
			loggedUser = CsvMisc.loadUserData(usrPath+username+".csv", username, password, delimeter);
			

			System.out.println(loggedUser.getSourceLanguage());

			setSrcLang(loggedUser.getSourceLanguage());
			

			System.out.println(lang_pack.get("welcome")+username);

			

			sourceLanguageKey = loggedUser.getSourceLanguage();
			targetLanguageKey = loggedUser.getTargetLanguage();

			courseKey = sourceLanguageKey + "-" + targetLanguageKey;

			

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

			courseKey = sourceLanguageKey + "-" + targetLanguageKey;

			loggedUser.addCourse(courseKey);
			CsvMisc.saveUserData(loggedUser, usrPath, delimeter);

		}

		
		csvName = courseKey + ".csv";
		pathToLessonListCsv = lesPath + csvName;
		pathToWordListCsv = dicPath + csvName;


		/**
		 * Optionally, we could have different dictionaries for different lessons.
		 * We could also have different dictionaries for different lesson parts but i will stick with the idea above and use an offset.
		 */
		final String[][] dictionary = readFromCsv(pathToWordListCsv, numberOfTuples);

		/**
		 * 
		 */
		final ArrayList<String> lessons = CsvMisc.readCsvKeysToIAL(pathToLessonListCsv, delimeter);
		Map<String, int[]> lessonIndexes = CsvMisc.readCsvToLessonIndexMap(pathToLessonListCsv, delimeter);

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
				printProgressBar(loggedUser.getProgress(courseKey).getLessonTimesDone(lessons.get(i))*12, 20);
				
			}
			System.out.println(lang_pack.get("choose lesson"));

			System.out.print(prompt);
			int selection = input.nextInt();

			if (selection==-1) break;

			loggedUser.changeLessonTimesDone(courseKey, lessons.get(selection),
				exerciseSession(dictionary, 
					lessonIndexes.get(lessons.get(selection))[0], 
					lessonIndexes.get(lessons.get(selection))[1]));

			CsvMisc.saveUserData(loggedUser, usrPath, delimeter);

		}

		input.close();

	}

	/**
	 * TODO: definitely write a better session function!
	 * @param dictionary
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static int exerciseSession(final String[][] dictionary, int startIndex, int endIndex){
		//pick a number of words from dictionary
		final ArrayList<String[]> wordList = new ArrayList<String[]>(); 

		int numberOfWords = endIndex - startIndex;

		for(int i=0;i<numberOfWords;i++){
			wordList.add(dictionary[startIndex + 1 + i]);
		}

		int rating = 5;

		final Random random = new Random();
		int score=0; 
		int i;
		int c = numberOfWords;
		//add multiplier
		while(score<360){
			printProgressBar(score, pBarLength);
			i = random.nextInt(c);
			final String[] tuple = wordList.get(i);

			int exerciseScore;
			if(random.nextInt()%2==0){
				exerciseScore = writeOutExercise(tuple);
			} else {
				exerciseScore = blockExercise(tuple);
			}


			if(exerciseScore==1){
				wordList.remove(i);
				c--;
				score+=exerciseScore*(scoreLimit/numberOfWords);
			}

		}
		printProgressBar(score, pBarLength);
		System.out.println(lang_pack.get("success"));
		return rating;
	}

	//not really an exercise
	//debunked for now.
	public static int informationExercise(final String[] tuple){

		System.out.println(tuple[0] + ": " + tuple[1]);

		//TODO: add this to the dictionaries.
		System.out.println("Zaten biliyorum <N>. Bilmiyorum, öğret <Y>.");

		final String answer = waitForAcceptedAnswer(Arrays.asList("y", "n"));
		if(answer.equals("y")){
			System.out.println(lang_pack.get("let's learn") +"\n");
			return 0;
		} else {
			System.out.println(lang_pack.get("ignored") + tuple[1] +"\n");
			return 1;
		}
		
	}



	public static int writeOutExercise(final String[] tuple){

		System.out.println(lang_pack.get("translate to " + targetLanguageKey));
		System.out.println(tuple[0]);

		final Scanner input = new Scanner(System.in);
		System.out.print(prompt);
		final String answer = input.nextLine();
		if(answer.equals(tuple[1]) || answer.equals(tuple[1].replaceAll("ə", "é"))){
			System.out.println(lang_pack.get("right answer") +"\n");
			return 1;
		} else {
			System.out.println(lang_pack.get("wrong answer") + tuple[1] +"\n");
			return 0;
		}
		
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
					return 1;
				} else {
					System.out.println(lang_pack.get("wrong answer") + tuple[1] +"\n");
					return 0;
				}
			}
			
		}
	}


	public static String[][] readFromCsv(final String pathToCsv, final int numberOfTuples){

		final String[][] dictionary = new String[numberOfTuples][2];

		try {
			final BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));

			String row; int i=0;
			while (i<numberOfTuples+1 && (row = csvReader.readLine()) != null) {

				if(i==0) {i++; continue;}

				final String[] data = row.split(";");
				dictionary[i-1][0] = data [0];
				dictionary[i-1][1] = data [1];
 				i++;
				
			}
			csvReader.close();
			


		}
		catch (final FileNotFoundException e){
			System.out.println(e);
		}
		catch (final IOException e){
			System.out.println(e);
		}

		return dictionary;

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
	

	//TODO: change delimeter to ";"
	public static void writeToCsv(final String pathToCsv, final String s){

		try {
			final FileWriter csvWriter = new FileWriter(pathToCsv);
			csvWriter.append(s);
			csvWriter.flush();
			csvWriter.close();

		}
		catch (final FileNotFoundException e){
			System.out.println(e);
		}
		catch (final IOException e){
			System.out.println(e);
		}
	}

	

	public static void printProgressBar(final int score, final int length){
		System.out.print(ANSI_BLUE);
		System.out.print("");
		for(int i=0;i<length;i++){
			if(i<score/(scoreLimit/length)) System.out.print("▮");
			else System.out.print("▯");
		}
		System.out.println(ANSI_RESET);
		System.out.println("");
	}


	/**
	 * TODO: read these dictionaries from files.
	 * write a function to initialize maps from json/csv files.
	*/

	public static Map<String, String> lang_Tr = new HashMap<String, String>() {{
		put("translate to tr",					"Bu cümleyi Türkiye Türkçesi'ne çeviriniz.");
		put("translate to az",					"Bu cümleyi Azerbaycan Türkçesi'ne çeviriniz.");
		put("translate to kz",					"Bu cümleyi Kazakistan Türkçesi'ne çeviriniz.");
        put("choose lesson",					"^Listeden bir ders seçiniz. (Sadece dersin numarasını giriniz.)");
        put("lesson",							"Ders ");
		put("success",							"Egzersizi tamamladınız, tebrikler!");
		put("right answer",						"Tebrikler, doğru cevap!");
		put("wrong answer",						"Yanlış cevap, doğrusu: ");
		put("welcome",							"Hoş geldin ");
		put("enter username",					"Kullanıcı adını girin.");
		put("let's learn",						"Bu kelimeyi öğrenelim: ");
		put("ignored",							"Bu kelimeyi bir daha görmeyeceksin: ");
		put("enter an integer value",			"Lütfen eklemek istediğiniz kelimenin numarasını giriniz. ");
		
	}};
	
	public static Map<String, String> lang_Az = new HashMap<String, String>() {{
		put("translate to tr",					"Bu cümləni Azərbaycan türk dilinə tərcümə edin.");
		put("translate to az",					"Bu cümləni Azərbaycan türkcəsinə tərcümə edin.");
		put("translate to kz",					"Bu cümləni qazax türkcəsinə tərcümə edin.");
        put("choose lesson",					"^Siyahıdan bir kurs seçin. (Sadəcə kurs nömrəsini daxil edin.)");
        put("lesson",							"Ders ");
		put("success",							"Məşqi başa vurdunuz, afərin!");
		put("right answer",						"Afərin, düzgün cavab!");
		put("wrong answer",						"Yanlış cavab, əslində: ");
		put("welcome",							"Xoş gəldin ");
		put("enter username",					"İstifadəçi adınızı daxil edin.");
		put("let's learn",						"Bu sözü öyrənək:");
		put("ignored",							"Bu sözü bir daha görməyəcəksiniz: ");
		put("enter an integer value",			"Lütfen eklemek istediğiniz kelimenin numarasını giriniz. "); //TODO:
		
	}};
	
	public static Map<String, String> lang_Kz = new HashMap<String, String>() {{
		put("translate to tr",					"Түркия осы сөйлемді бұраңыз.");
		put("translate to az",					"Бұл сөйлемді әзірбайжан түрікшесіне аударыңыз.");
		put("translate to kz",					"Осы сөйлемді қазақ түрікшесіне аударыңыз.");
        put("choose lesson",					"^Тізімнен курс таңдаңыз. (Курстың нөмірін енгізіңіз.)");
        put("lesson",							"Курс ");
		put("success",							"Сіз жаттығуды аяқтадыңыз, құттықтаймыз!");
		put("right answer",						"Құттықтаймыз, дұрыс жауап!");
		put("wrong answer",						"Қате жауап, нақты: ");
		put("welcome",							"Қош келдіңіздер ");
		put("enter username",					"Пайдаланушы атын енгізіңіз.");
		put("let's learn",						"Осы сөзді білейік: ");
		put("ignored",							"Бұл сөзді енді ешқашан көрмейсіз: ");
		put("enter an integer value",			"Lütfen eklemek istediğiniz kelimenin numarasını giriniz. "); //TODO:
		
	}};

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