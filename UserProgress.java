import java.util.*;

class UserProgress {

    public String pathToDicDir  = "dictionaries/";
    public String pathToLesDir  = "lessons/";
    public String delimeter     = ";";


    //note: courseKey: "<source language>-<target language>"
    //note: lessonKey: "<lesson number>-<part number>"

    //key: "<courseKey>:<lessonkey>" (i.e. "az-tr:2-1")
    private Map<String, Integer> lessonTimesDone;

    private Map<String, Double> wordStrength;

    // Constructor
    public UserProgress(String courseKey){
        this.lessonTimesDone    = new HashMap<String, Integer>();
        this.wordStrength       = new HashMap<String, Double>();

        this.lessonTimesDone    = CsvMisc.readCsvKeysToSIM(pathToLesDir + courseKey + ".csv", delimeter);
        this.wordStrength       = CsvMisc.readCsvValsToSDM(pathToDicDir + courseKey + ".csv", delimeter);
        
    }


    
    public Map<String, Integer> getLessonTimesDone() {
		return this.lessonTimesDone;
    }

    public Map<String, Double> getWordStrength() {
		return this.wordStrength;
    }

    public int getLessonTimesDone(String lessonKey) {
		return this.lessonTimesDone.get(lessonKey);
    }

    public void setLessonTimesDone(String lessonKey, int value) {
		this.lessonTimesDone.put(lessonKey, value);
    }



    //this assumes no duplicate words. 
    //somehow implement a system that handles duplicate words.
    //de, de_2 etc. split and ignore the rest when displaying.
    public Double getWordStrength(String word) {
		return this.wordStrength.get(word);
    }

    public void setWordStrength(String word, Double value) {
		this.wordStrength.put(word, value);
    }

	public void changeLessonTimesDone(String lessonKey, int amount) {
		this.lessonTimesDone.put(lessonKey, this.lessonTimesDone.get(lessonKey)+amount);
    }

    public void changeWordStrength(String word, Double amount) {
		this.wordStrength.put(word, this.wordStrength.get(word)+amount);
    }

    
}