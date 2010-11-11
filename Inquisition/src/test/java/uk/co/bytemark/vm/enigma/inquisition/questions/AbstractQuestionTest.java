package uk.co.bytemark.vm.enigma.inquisition.questions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

abstract public class AbstractQuestionTest {
    protected Question            exampleQuestion;

    protected static final String EXAMPLE_QUESTION_TEXT    = "Question <CopyToExplanation>stuff to copy</CopyToExplanation>";
    protected static final String EXAMPLE_EXPLANATION_TEXT = "Explanation <CopyFromQuestion/> end of explanation";

    @Test
    public void exampleQuestionNotNull() throws Exception {
        assertNotNull(exampleQuestion);
    }

    @Test
    public void shouldCopyAcrossFromQuestionToExplanation() throws Exception {
        String substitutedExplanationText = exampleQuestion.getSubstitutedExplanationText();
        assertEquals("Explanation stuff to copy end of explanation", substitutedExplanationText);
    }

}
