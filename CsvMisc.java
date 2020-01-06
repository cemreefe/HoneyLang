import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CsvMisc {

    /**
     * csv KEYS to arraylist
     * 
     * @param csvFile
     * @param delimeter
     * @return
     */
    public static ArrayList<String> readCsvKeysToIAL(String csvFile, String delimeter) {
        String line = "";
        ArrayList<String> list = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] country = line.split(delimeter);

                list.add(country[0]);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * csv KEYS to string-integer map
     * 
     * @param csvFile
     * @param delimeter
     * @return
     */
    public static Map<String, Integer> readCsvKeysToSIM(String csvFile, String delimeter) {
        String line = "";
        Map<String, Integer> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] country = line.split(delimeter);

                list.put(country[0], 0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * csv to string-integer map
     * 
     * @param csvFile
     * @param delimeter
     * @return
     */
    public static Map<String, Integer> readCsvToSIM(String csvFile, String delimeter) {
        String line = "";
        Map<String, Integer> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] country = line.split(delimeter);

                list.put(country[0], Integer.parseInt(country[1]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * csv to string-string map
     * 
     * @param csvFile
     * @param delimeter
     * @return
     */
    public static Map<String, String> readCsvToSSM(String csvFile, String delimeter) {
        String line = "";
        Map<String, String> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] country = line.split(delimeter);

                list.put(country[0], country[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * csv to string-double map
     * 
     * @param csvFile
     * @param delimeter
     * @return
     */
    public static Map<String, Double> readCsvToSDM(String csvFile, String delimeter) {
        String line = "";
        Map<String, Double> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                /**
                 * lesson structure: <lesson name>;<start index>;<end index>
                 */

                String[] country = line.split(delimeter);

                list.put(country[0], Double.parseDouble(country[1]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * csv KEYS to string-double map
     * 
     * @param csvFile
     * @param delimeter
     * @return
     */
    public static Map<String, Double> readCsvValsToSDM(String csvFile, String delimeter) {
        String line = "";
        Map<String, Double> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] country = line.split(delimeter);

                // 1 because we want the target language's words
                list.put(country[1], 0.0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void saveUserData(User user, String usersPath, String delimeter) {

        // TODO: is this copying harmful?
        String path = usersPath + user.getName() + ".csv";
        byte[] hashedPassword = user.getHashedPassword();
        ArrayList<String> validCourses = User.getValidCourses();
        ArrayList<String> activeCourses = user.getActiveCourses();
        Map<String, UserProgress> allProgress = user.getAllProgress();

        /**
         * hashedPassword validcourse1;validcourse2 lesson0;lv0 lesson1;lv1 ...
         * word0;ws0 word1;ws1 ...
         * 
         */

        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(path);
            csvWriter.append(Arrays.toString(hashedPassword));
            csvWriter.append("\n");

            for (int i = 0; i < validCourses.size(); i++) {
                csvWriter.append(validCourses.get(i));
                if (i != validCourses.size() - 1) {
                    csvWriter.append(delimeter);
                }
            }

            csvWriter.append("\n");

            for (int i = 0; i < activeCourses.size(); i++) {
                csvWriter.append(activeCourses.get(i));
                if (i != activeCourses.size() - 1) {
                    csvWriter.append(delimeter);
                }
            }

            csvWriter.append("\n");

            csvWriter.append(user.getSourceLanguage() + "-" + user.getTargetLanguage() + "\n");

            for (Map.Entry<String, UserProgress> pEntry : allProgress.entrySet()) {
                csvWriter.append("!lessons;" + pEntry.getKey() + "\n");

                UserProgress progress = pEntry.getValue();

                for (Map.Entry<String, Integer> entry : progress.getLessonTimesDone().entrySet()) {
                    csvWriter.append(entry.getKey() + delimeter + entry.getValue() + "\n");
                }

                csvWriter.append("!words;" + pEntry.getKey() + "\n");

                for (Map.Entry<String, Double> entry : progress.getWordStrength().entrySet()) {
                    csvWriter.append(entry.getKey() + delimeter + entry.getValue() + "\n");
                }

            }

            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static boolean checkUserPassword(String userPath, String password) throws NoSuchAlgorithmException {

        MessageDigest hasher = MessageDigest.getInstance("SHA-256");
        byte[] request = hasher.digest(password.getBytes(StandardCharsets.UTF_8));
        String reprStr = Arrays.toString(request);

        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(userPath))) {

            line = br.readLine();

            return line.equals(reprStr);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
    }

    public static User loadUserData(String userPath, String username, String password, String delimeter)
            throws NoSuchAlgorithmException {

        
        User loadedUser = new User(username,password);


        if(checkUserPassword(userPath, password)){
    

            String line = "";
            try (BufferedReader br = new BufferedReader(new FileReader(userPath))) {
    
                int i=0;
                String currentKey = "";
                String currentCourse = "";
                while ((line = br.readLine()) != null) {

                    String[] country = line.split(delimeter);



                    if(i==2){
                        //activecourses
                        for(int j=0;j<country.length;j++){
                            if(User.validCourses.contains(country[j])){
                                loadedUser.addCourse(country[j]);
                            }
                        }
                    } else if(i==3){
                        loadedUser.setCurrentCourse(line);
                    } else if(line.startsWith("!")){
                        currentKey = country[0];
                        currentCourse = country[1];
                    } else if(currentKey.equals("!lessons")){
                        loadedUser.changeLessonTimesDone(currentCourse, country[0], Integer.parseInt(country[1]));
                    } else if(currentKey.equals("!words")) {
                        loadedUser.changeWordStrength(currentCourse, country[0],  Double.parseDouble(country[1]));
                    }

                    i++;
                }
    
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            return new User("!wrongpassword", "");
        }

        return loadedUser;

    }

    public static Map<String, int[]> readCsvToLessonIndexMap(String csvFile, String delimeter) {
        String line = "";
        Map<String, int[]> list = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] country = line.split(delimeter);

                int startIndex = Integer.parseInt(country[1]);
                int endIndex = Integer.parseInt(country[2]);

                int[] indexes = {startIndex, endIndex};

                list.put(country[0], indexes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String[][] readDicFromCsv(final String pathToCsv, final int numberOfTuples){

		final String[][] dictionary = new String[numberOfTuples][2];

		try {
			final BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));

			String row; int i=0;
			while (i<numberOfTuples+1 && (row = csvReader.readLine()) != null) {

				if(i==0) {i++; continue;}

				final String[] data = row.split(";");
				dictionary[i-1][0] = data [0];
                dictionary[i-1][1] = data [1];
                if(data.length==3){
                    dictionary[i-1][2] = data [2];
                }
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
    
    public static void writeStringToCsv(final String pathToCsv, final String s){

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
}
