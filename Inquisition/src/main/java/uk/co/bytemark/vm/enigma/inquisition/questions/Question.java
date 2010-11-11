package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.io.Serializable;

public interface Question extends Serializable {

    String getQuestionText();

    String getSubstitutedQuestionText();

    String getExplanationText();

    String getSubstitutedExplanationText();

    String getQuestionTypeName();

    boolean isCorrect(Answer answer);

    Answer initialAnswer();

}
