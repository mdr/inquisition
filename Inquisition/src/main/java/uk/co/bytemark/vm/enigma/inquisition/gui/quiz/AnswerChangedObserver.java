package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import uk.co.bytemark.vm.enigma.inquisition.questions.Answer;

/**
 * An interface to allow {@link QuestionPanel}s to signal a change in the user's answer.
 */
public interface AnswerChangedObserver {
    abstract public void answerChanged(Answer answer);
}
