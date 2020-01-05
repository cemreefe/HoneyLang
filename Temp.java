import java.io.*;  
import java.util.*; 



/**
 * user data is independent of language!!!!
 */


public class Temp {

	public static String prompt = ">";

	public static int scoreLimit = 360;

	public static Map<String, String> lang_pack; 

	public static String targetLanguageKey;
	public static String sourceLanguageKey;

	public static int pBarLength;	


	/**
	 * read input from console alas input not accepted
	 * @param acceptedAnswers : a list of accepted input strings
	 * @return
	 */
	public static String waitForAcceptedAnswer(final List<String> acceptedAnswers){
		
		String inputBuffer = "";
		while(!(acceptedAnswers.contains(inputBuffer) || acceptedAnswers.contains(inputBuffer.toLowerCase()))){
			final Scanner input = new Scanner(System.in);
			System.out.println(prompt);
			inputBuffer = input.nextLine();
		}
		return inputBuffer;
	}
	
	public static void main(final String[] args) {

		final String dicPath = "dictionaries/";
		
		/**
		 * numberOfTuples:	number of word pairs to be read from the csv file for a lesson.
		 * wordsPerLesson:	number of Words per lesson (or part.) 
		 * 	!!! disclaimer: lessons and parts are not yet implemented differently.
		 * temporary buffer for reading the console input.
		 */
		final int numberOfTuples = 100;
		final int wordsPerLesson = 10;
		final int numberOfLessons = numberOfTuples/wordsPerLesson;


		pBarLength = wordsPerLesson*2;
		
		/**
		 * for console input
		 */
		final Scanner input = new Scanner(System.in);


		/**
		 * Prompt to choose source language
		 */
		System.out.println("Uygulama dilini seçiniz.\n[TR]\t[AZ]\t[KZ]");
		sourceLanguageKey = waitForAcceptedAnswer(new ArrayList<String>( Arrays.asList("tr", "az", "kz")));

		if(sourceLanguageKey.equals("tr")){
			lang_pack = lang_Tr;
		} else if (sourceLanguageKey.equals("az")) {
			lang_pack = lang_Az;
		} else if (sourceLanguageKey.equals("kz")) {
			lang_pack = lang_Kz;
		}

		/**
		 * Prompt to choose target language
		 */
		System.out.println("Öğrenmek istediğiniz dili seçiniz.\n[TR]\t[AZ]\t[KZ]");
		targetLanguageKey = waitForAcceptedAnswer(new ArrayList<String>( Arrays.asList("tr", "az", "kz")));

		String csvName = sourceLanguageKey + "-" + targetLanguageKey + ".csv";
		String pathToCsv = dicPath + csvName;

		/**
		 * get username, if user exists, load data
		 * else create new user.
		 */


		System.out.println(lang_pack.get("enter username"));
		System.out.println(prompt);
		String username = input.next();
		
		if(userExists(username)){
			System.out.println(lang_pack.get("welcome")+username);
			loadUserData(username);
			System.out.println("loaded");
		} else {
			saveUser(username, sourceLanguageKey, targetLanguageKey);
		}


		/**
		 * Optionally, we could have different dictionaries for different lessons.
		 * We could also have different dictionaries for different lesson parts but i will stick with the idea above and use an offset.
		 */
		final String[][] dictionary = readFromCsv(pathToCsv, numberOfTuples);

		while(true){
			/**
			 * Print out the lesson layout.
			 *
			 * to do: lessons should have sublessons (namely parts.)    or should they ??
			 * These do not constitute a difference in implementation
			 * but a difference in respresentation.
			 */
			for(int i=0;i<numberOfLessons;i++){
				System.out.print(lang_pack.get("lesson") + (i+1) + "\t");
				//neden 35?
				printProgressBar(stoia(userData.get("progress"))[i]*12, 20);
			}
			System.out.println(lang_pack.get("choose lesson"));
			


			System.out.print(prompt);
			int selection = input.nextInt();

			if (selection==-1) break;

			
			
			changeElementInProgress(selection, 
				exerciseSession(wordsPerLesson, dictionary, (selection-1)*wordsPerLesson));

			
			saveUser(username, sourceLanguageKey, targetLanguageKey);

		}

		
		input.close();

	}

	public static int exerciseSession(final int numberOfWords, final String[][] dictionary, final int offset){
		//pick a number of words from dictionary
		final ArrayList<String[]> wordList = new ArrayList<String[]>(); 

		for(int i=0;i<numberOfWords;i++){
			wordList.add(dictionary[offset + i]);
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
			int answer = input.nextInt();
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

	public static void loadUserData(String username){
		try {
			BufferedReader csvReader = new BufferedReader(new FileReader("users/user_"+username+".efe"));

			String row;
			while ((row = csvReader.readLine()) != null) {

				String[] data = row.split(";");
				System.out.println(row);
				userData.put(data[0], data[1]);
				
			}
			csvReader.close();

		}
		catch (final FileNotFoundException e){
			System.out.println(e);
		}
		catch (final IOException e){
			System.out.println(e);
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

	public static void changeElementInProgress(int lessonNumber1b, int changeInValue){
		int[] a = stoia(userData.get("progress"));
		a[lessonNumber1b-1]+=changeInValue;
		userData.put("progress", iatos(a));
	}

	public static boolean userExists(String username){
		try {
			final BufferedReader csvReader = new BufferedReader(new FileReader("users/allUsers.efe"));

			String row;
			while ((row = csvReader.readLine()) != null) {

				if(row.equals(username)){
					csvReader.close();
					return true;
				} 
				
			}
			csvReader.close();
			return false;

		}
		catch (final FileNotFoundException e){
			System.out.println(e);
		}
		catch (final IOException e){
			System.out.println(e);
		}

		//if we return false when exceptions occur, we will later overwrite user data
		//if we return true we will later catch a fileNotFoundException
		return true;

	}
	

	/**
	 * creates new user when username not taken.
	 * @param username : 
	 * @param sourceLanguageKey :
	 * @param targetLanguageKey :
	 * @return
	 */
	public static void saveUser(String username, String sourceLanguageKey, String targetLanguageKey){

		String s = "password;" 				+ userData.get("password") + "\n"
					+ "progress;" 			+ userData.get("progress") + "\n"
					+ "source language;" 	+ sourceLanguageKey + "\n"
					+ "target language;" 	+ targetLanguageKey;

		try {
			final FileWriter csvWriter = new FileWriter("users/user_"+username+".efe");
			csvWriter.append(s);
			csvWriter.flush();
			csvWriter.close();

			if(!userExists(username)){
				BufferedWriter usersWriter = new BufferedWriter(new FileWriter("users/allUsers.efe", true));
				usersWriter.append(username + "\n");
				usersWriter.close();
			}

		}
		catch (final FileNotFoundException e){
			System.out.println(e);
		}
		catch (final IOException e){
			System.out.println(e);
		}

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

	public static Map<String, String> userData = new HashMap<String, String>() {{
		put("password",							"%");
		put("progress",							"0,0,0,0,0,0,0,0,0,0");
		put("source language",					"");
		put("target language",					"");
	}};

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