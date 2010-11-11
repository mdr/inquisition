package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.Arrays;

import org.junit.Test;

public class QuestionSetTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullNameInConstructorThrowsException() {
        new QuestionSet(null, "description", 120, "category", Arrays.<Question> asList());
    }

    @Test(expected = RuntimeException.class)
    public void nullsInQuestionListThrowsExceptions() {
        new QuestionSet("name", "description", 120, "category", Arrays.<Question> asList((Question) null));
    }

}
