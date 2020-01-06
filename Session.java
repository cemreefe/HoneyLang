import java.util.*;

class Session {

    User user;
    Map<String, String> lang_pack;
    String prompt = ">";
    int pBarLength = 20;
    String lessonKey;

    public Session(User user){
        this.user = user;
        
        switch (this.user.getSourceLanguage()){
            case "tr": 
                this.lang_pack = LangPack.lang_Tr;
                break;
            case "az": 
                this.lang_pack = LangPack.lang_Az;
                break;
            case "kz": 
                this.lang_pack = LangPack.lang_Kz;
                break;
        }
    }

    // Constructor
    public void startSession(final String[][] dictionary, String lessonKey, int startIndex, int endIndex , int scoreLimit){

        this.lessonKey = lessonKey;

        Random random = new Random();

        //pick a number of words from dictionary
        final ArrayList<String[]>   sentenceList    = new ArrayList<String[]>(); 
        final ArrayList<Integer>    repetitionsLeft = new ArrayList<Integer>(); 

        int numberOfWords = endIndex - startIndex;

        for(int i=0;i<numberOfWords;i++){
            sentenceList.add(dictionary[startIndex + 1 + i]);
            repetitionsLeft.add(2);
		}

        // exercise will be over when score reaches scoreLimit
        int score=0; 
        
        /** Desired behavior: 
         * 
         * sentenceList contains Sentences.
         * 
         * Two types of sentences:
         * L: Learn sentences. These sentences will first be introduced to the User, before asking them to translate.
         * *: Regular sentences. These sentences' translations will directly be asked.
         * 
         * [<SrcSentence>,<DstSentence>,"L"]
         * [<SrcSentence>,<DstSentence>,"*"]  Notice: hints are stored in a different file
         * 
         * !: no shuffling in learning exercise
         * !: shuffling in practice exercise
         * 
         * if L sentence, will be shown only once before being asked.
         * every sentence will be asked at least twice.
         * 
         * if answer correct, decrease repetitions by one
         * if answer wrong, don't decrease repetitions.
         * 
         * at every decrease, check if decrease makes the value 0.
         * if yes remove.
         * 
         * what we need at the end:
         * 1) new strengths for the words.
         * 2) a score for the session.
         * 
         */

        while(score<scoreLimit){
			Misc.printProgressBar(score, scoreLimit, pBarLength);
			
            final String[] sentenceData = sentenceList.get(0);

			int exerciseScore=0;
            
            String typeToken = sentenceData[2];
            System.out.println(typeToken);

            switch (typeToken) {
                // normal sentence
                case "*" :
                    exerciseScore = normalExercise(sentenceData);

                    if(exerciseScore==1){

                        repetitionsLeft.set(0,repetitionsLeft.get(0)-1);
                        if(repetitionsLeft.get(0)==0){
                            repetitionsLeft.remove(0);
                            sentenceList.remove(0);
                        } else {
                            int randVal = random.nextInt(repetitionsLeft.size());
                            // move current item to avoid repetition.
                            // move both repetitionsLeft & sentenceList value.
                            repetitionsLeft.add(randVal, repetitionsLeft.remove(0));
                            sentenceList.add(randVal, sentenceList.remove(0));
                        }
                        score+=exerciseScore*(scoreLimit/numberOfWords);
                    }
                    break;

                // learn exercise
                case "L" :
                    
                    learnExercise(sentenceData);

                    //no moving around.

                    // Turn into normal exercise
                    sentenceData[2] = "*";
                    break;
            }

            if(exerciseScore==-1){
                return;
            }

        }
        
		Misc.printProgressBar(score, scoreLimit, pBarLength);
		System.out.println(this.lang_pack.get("success"));

    }

    private int normalExercise(String[] sentenceData){

        Random random = new Random();
        int choice = random.nextInt(1);
            
        switch (choice){
            case 0:
                return writeOutExercise(sentenceData);
        }

        // should be unreachable
        return 0;
        
    }

    private void learnExercise(String[] sentenceData){

        System.out.println(lang_pack.get("let's learn")+"\n");
        System.out.println("\"" + sentenceData[0] + "\" : \"" + sentenceData[1] + "\"");
        System.out.println(lang_pack.get("press enter to continue"));
        Scanner input = new Scanner(System.in);
        input.nextLine();

    }

    private int writeOutExercise(String[] sentenceData){

        //TODO: should handle strengths

		System.out.println(lang_pack.get("translate to " + this.user.getTargetLanguage()));
		System.out.println(sentenceData[0]);

		int response;
		
		System.out.print(prompt);
		Scanner input = new Scanner(System.in);
        String answer = input.nextLine();

        if (answer.equals(LangPack.commands.get("exit"))){
            // exit
            return -1;

        } else if(answer.equals(sentenceData[1]) || answer.equals(sentenceData[1].replaceAll("ə", "é"))){
            System.out.println(lang_pack.get("right answer") +"\n");
            
            //increment strength
            this.user.getAllProgress().get(this.user.getSourceLanguage()+"-"+this.user.getTargetLanguage()).changeWordStrength(sentenceData[1], 0.5);

			response = 1;
		} else {
            System.out.println(lang_pack.get("wrong answer") + sentenceData[1] +"\n");
            
            // decrement strength
            this.user.getAllProgress().get(this.user.getSourceLanguage()+"-"+this.user.getTargetLanguage()).changeWordStrength(sentenceData[1], -0.7);

			response = 0;
        } 
        
        this.user.getAllProgress().get(this.user.getSourceLanguage()+"-"+this.user.getTargetLanguage()).changeLessonTimesDone(lessonKey, 1);
		return response;
    }







    // Getter methods


    // Setter methods


}