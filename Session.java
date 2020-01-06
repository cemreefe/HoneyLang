import java.util.*;

class Session {

    // Constructor
    public Session(final String[][] dictionary, int startIndex, int endIndex , int scoreLimit){
        //pick a number of words from dictionary
        final ArrayList<String[]>   sentenceList    = new ArrayList<String[]>(); 
        final ArrayList<Integer>    repetitionsLeft = new ArrayList<Integer>(); 

        int numberOfWords = endIndex - startIndex;

        for(int i=0;i<numberOfWords;i++){
            sentenceList.add(dictionary[startIndex + 1 + i]);
            repetitionsLeft.add(2);
		}

        
        //to pick a word randomly
        final Random random = new Random();

        // the value to be returned. this course's progress will be incremented by this amount
        int rating = 5;
        
        // exercise will be over when score reaches scoreLimit
        int score=0; 
        
        // for iteration
        int i;
        
        // for counting down words
        int c = numberOfWords;
        
        /**
         * Desired behavior: 
         * sentenceList contains Sentences.
         * 
         * Two types of sentences:
         * yl: YesLearn sentences. These sentences will first be introduced to the User, before asking them to translate.
         * nl: NoLearn sentences. These sentences' translations will directly be asked.
         * 
         * [<SrcSentence>,<DstSentence>,"yl"]
         * [<SrcSentence>,<DstSentence>,"nl"]
         * 
         * !: no shuffling in learning exercise
         * !: shuffling in practice exercise
         * 
         * if yl sentence will be shown only once before being asked.
         * every sentence will be asked at least twice.
         * 
         * if answer correct, decrease repetitions by one
         * if answer wrong, don't decrease repetitions.
         * 
         * at every decrease, check if decrease makes the value 0.
         * if yes remove.
         * 
         * what we need at the end:
         * 1) strengths for the words.
         * 2) a score for the lesson.
         * 
         */



    }







    // Getter methods


    // Setter methods


}