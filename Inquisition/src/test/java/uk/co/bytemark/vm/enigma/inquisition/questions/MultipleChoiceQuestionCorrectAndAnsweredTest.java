package uk.co.bytemark.vm.enigma.inquisition.questions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

public class MultipleChoiceQuestionCorrectAndAnsweredTest {

    private MultipleChoiceQuestion question;

    private final Option           option1 = new Option("Option a", false, 1);

    private final Option           option2 = new Option("Option b", false, 2);

    private final Option           option3 = new Option("Option c", true, 3);

    private final Option           option4 = new Option("Option d", false, 4);

    private final Option           option5 = new Option("Option e", true, 5);

    @Before
    public void setUp() throws Exception {
        List<Option> options = Arrays.asList(option1, option2, option3, option4, option5);
        question = new MultipleChoiceQuestion("Question", "Explanation", options, true, false);

    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfOptionSetContainsNulls() throws Exception {
        new MultipleChoiceAnswer(Collections.<Option> singleton(null));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionsIfNullsInConstructor() throws Exception {
        new MultipleChoiceAnswer(null);
    }

    @Test
    public void shouldRecogniseCorrectAnswer() {
        assertTrue(isAnswerCorrect(option3, option5));
    }

    @Test
    public void shouldRecogniseIncorrectAnswer() {
        assertFalse(isAnswerCorrect(option3, option5, option1));
        assertFalse(isAnswerCorrect(option5));
        assertFalse(isAnswerCorrect(option3));
        assertFalse(isAnswerCorrect());
        assertFalse(isAnswerCorrect(option2));
        assertFalse(isAnswerCorrect(option1, option2, option3, option4, option5));
    }

    private boolean isAnswerCorrect(Option... options) {
        Set<Option> optionSet = Utils.makeSet(options);
        MultipleChoiceAnswer answer = new MultipleChoiceAnswer(optionSet);
        return question.isCorrect(answer);
    }

    @Test
    public void shouldRecognisedWhenQuestionHasBeenAnsweredWhenNumberOfOptionsIsKnown() throws Exception {
        assertTrue(isQuestionAnswered(true, option1, option2));
        assertTrue(isQuestionAnswered(true, option3, option2));
        assertTrue(isQuestionAnswered(true, option3, option5));
        assertTrue(isQuestionAnswered(true, option4, option5));

        assertFalse(isQuestionAnswered(true));
        assertFalse(isQuestionAnswered(true, option1));
        assertFalse(isQuestionAnswered(true, option3));
        assertFalse(isQuestionAnswered(true, option5));
        assertFalse(isQuestionAnswered(true, option1, option3, option5));
        assertFalse(isQuestionAnswered(true, option1, option2, option4));
        assertFalse(isQuestionAnswered(true, option1, option5, option4));

    }

    @Test
    public void shouldRecognisedWhenQuestionHasBeenAnsweredWhenNumberOfOptionsIsUnknown() throws Exception {
        assertTrue(isQuestionAnswered(false, option1));
        assertTrue(isQuestionAnswered(false, option1, option2));
        assertTrue(isQuestionAnswered(false, option1, option2, option3));
        assertTrue(isQuestionAnswered(false, option1, option2, option3, option4));
        assertTrue(isQuestionAnswered(false, option1, option2, option3, option4, option5));

        assertFalse(isQuestionAnswered(false));
    }

    private boolean isQuestionAnswered(boolean stateNumberOfOptionsNeeded, Option... options) {
        Set<Option> optionSet = Utils.makeSet(options);
        MultipleChoiceAnswer answer = new MultipleChoiceAnswer(optionSet);
        return question.isAnswered(answer, stateNumberOfOptionsNeeded);
    }

    @Test
    public void shouldAcceptEmptyOptionSet() throws Exception {
        MultipleChoiceAnswer answer = new MultipleChoiceAnswer(new HashSet<Option>());
        assertFalse(question.isAnswered(answer, true));
        assertFalse(question.isAnswered(answer, false));
        assertFalse(question.isCorrect(answer));
    }

    @Test
    public void shouldThrowExceptionIfOptionsNotConsistentWithQuestion() throws Exception {
        Option faultyOption1 = new Option("Not an option", false, 1);
        Option faultyOption2 = new Option("Option a", true, 1);
        Option faultyOption3 = new Option("Option c", true, 4);
        checkThrowsExceptionWithTheseOptions(faultyOption1);
        checkThrowsExceptionWithTheseOptions(faultyOption2);
        checkThrowsExceptionWithTheseOptions(faultyOption3);
        checkThrowsExceptionWithTheseOptions(faultyOption1, faultyOption2);
        checkThrowsExceptionWithTheseOptions(faultyOption1, faultyOption2, faultyOption3);
        checkThrowsExceptionWithTheseOptions(faultyOption1, option1);
        checkThrowsExceptionWithTheseOptions(faultyOption1, option2);
        checkThrowsExceptionWithTheseOptions(faultyOption1, faultyOption2, option1, option2);
    }

    private void checkThrowsExceptionWithTheseOptions(Option... options) {
        Set<Option> optionSet = Utils.makeSet(options);
        MultipleChoiceAnswer answer = new MultipleChoiceAnswer(optionSet);
        try {
            question.isAnswered(answer, false);
            fail("Should not allow options which are not part of question");
        } catch (IllegalArgumentException e) {
            // Exception expected from test
        }
        try {
            question.isCorrect(answer);
            fail("Should not allow options which are not part of question");
        } catch (IllegalArgumentException e) {
            // Exception expected from test
        }
    }

}
