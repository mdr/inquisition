/*
 * TestConfig.java
 * 
 * Created on 04 September 2006, 17:23
 */

package uk.co.bytemark.vm.enigma.inquisition.gui.misc;

import java.io.Serializable;

/**
 * Configuration options for a quiz.
 */
public class QuizConfig implements Serializable {

    private static final int NO_TIMER = -1;

    private final boolean shuffleQuestionOrder;

    private final boolean stateNumberOfOptionsNeededForMultipleChoice;

    private final boolean useTimer;

    private final int timeAllowed; // in seconds

    private QuizConfig(boolean shuffleQuestionOrder, boolean stateNumberOfOptionsNeededForMultipleChoice,
            boolean useTimer, int timeAllowed) {
        this.shuffleQuestionOrder = shuffleQuestionOrder;
        this.stateNumberOfOptionsNeededForMultipleChoice = stateNumberOfOptionsNeededForMultipleChoice;
        this.useTimer = useTimer;
        this.timeAllowed = timeAllowed;
    }

    public boolean shouldShuffleQuestionOrder() {
        return shuffleQuestionOrder;
    }

    public boolean shouldStateNumberOfOptionsNeededForMultipleChoice() {
        return stateNumberOfOptionsNeededForMultipleChoice;
    }

    public boolean isQuizTimed() {
        return useTimer;
    }

    public int getTimeAllowed() {
        if (!isQuizTimed())
            throw new IllegalArgumentException("Configured for no timer");
        return timeAllowed;
    }

    public static QuizConfig createWithTimer(boolean shuffleQuestionOrder,
            boolean stateNumberOfOptionsNeededForMultipleChoice, int timeAllowed) {
        if (timeAllowed <= 0)
            throw new IllegalArgumentException("Time must be greater than zero");
        return new QuizConfig(shuffleQuestionOrder, stateNumberOfOptionsNeededForMultipleChoice, true, timeAllowed);
    }

    public static QuizConfig createWithoutTimer(boolean shuffleQuestionOrder,
            boolean stateNumberOfOptionsNeededForMultipleChoice) {
        return new QuizConfig(shuffleQuestionOrder, stateNumberOfOptionsNeededForMultipleChoice, false, NO_TIMER);
    }

}
