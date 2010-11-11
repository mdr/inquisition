package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.Collections;

import org.junit.Test;

public class MultipleChoiceAnswerTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfOptionSetContainsNulls() throws Exception {
        new MultipleChoiceAnswer(Collections.<Option> singleton(null));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionsIfNullsInConstructor() throws Exception {
        new MultipleChoiceAnswer(null);
    }

}
