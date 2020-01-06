import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
//for sha-256
import java.security.NoSuchAlgorithmException;
import java.util.*;

class User {

    private String name;
    private byte[] hashedPassword;

    private Map<String,UserProgress> progress;
    private String sourceLanguage;
    private String targetLanguage;

    private MessageDigest hasher;

    static ArrayList<String> validCourses = new ArrayList<String>( Arrays.asList("tr-az", "az-tr"));
    
    private ArrayList<String> activeCourses = new ArrayList<String>();

    // Constructors
    // for new user
    public User(String name, String password) throws NoSuchAlgorithmException {
        this.hasher = MessageDigest.getInstance("SHA-256");

        this.name = name;
        this.hashedPassword = hasher.digest(password.getBytes(StandardCharsets.UTF_8));
        this.progress = new HashMap<String,UserProgress>();
    }

    // for loading existing user
    // deprecated
    public User(String name, String password, boolean isLoad){

        //load user data from path (pass first) 
        String saidPassword = "";

        if(saidPassword.equals(hasher.digest(password.getBytes(StandardCharsets.UTF_8)))){
            
        } else {
            System.out.println("Wrong password, try again!");
            this.name = "!wrongpassword";
        }
    }


    // Getter methods
    public String getName() {
        return name;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public Map<String,UserProgress> getAllProgress() {
        return progress;
    }

    public static ArrayList<String> getValidCourses() {
        return validCourses;
    }

    public ArrayList<String> getActiveCourses() {
        return activeCourses;
    }

    public UserProgress getProgress(String courseKey) {
        return progress.get(courseKey);
    }

    public byte[] getHashedPassword(){
        return this.hashedPassword;
    }



    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        byte[] encodedhash = hasher.digest(password.getBytes(StandardCharsets.UTF_8));
        //should i do arraycopy?
        this.hashedPassword = encodedhash;   
    }

    public void setCurrentCourse(String courseKey){
        String[] keys = courseKey.split("-");
        this.sourceLanguage = keys[0];
        this.targetLanguage = keys[1];
    }

    // Other methods

    public void addCourse(String courseKey){

        if(validCourses.contains(courseKey) 
            && !this.progress.containsKey(courseKey)){
                if(this.activeCourses.isEmpty()){
                    this.setCurrentCourse(courseKey);
                }
            this.progress.put(courseKey, new UserProgress(courseKey));
            this.activeCourses.add(courseKey);
        } else {
            System.out.println("Error: unable to add course. See error code: 42069");
        }
    }

    public void changeWordStrength(String courseKey, String word, Double amount){
        this.progress.get(courseKey).changeWordStrength(word, amount);
    }

    public void changeLessonTimesDone(String courseKey, String lesson, int amount){
        this.progress.get(courseKey).changeLessonTimesDone(lesson, amount);
    }

    public void removeCourse(String courseKey, String password){

        if(this.hashedPassword.equals(hasher.digest(password.getBytes(StandardCharsets.UTF_8))) 
            && this.progress.containsKey(courseKey)){

            this.progress.remove(courseKey);
        } else {
            System.out.println("Error: unable to remove course. See error code: 31690");
        }
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
	public static void writeToUsersList(String username){


		try {

			if(!User.userExists(username)){
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

}