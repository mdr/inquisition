package uk.co.bytemark.vm.enigma.inquisition.questions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MultipleChoiceQuestionTest extends AbstractQuestionTest {

    private MultipleChoiceQuestion multipleOptionModeQuestion;

    @Before
    public void setUp() throws Exception {

        multipleOptionModeQuestion = new MultipleChoiceQuestion("An HTML Question, including <i>tags</i>"
                + "and other entities &amp; &gt;", "Explanation , including <i>tags</i>\"\n"
                + "and other entities &amp; &gt;", makeTwoOptionsCorrectThreeIncorrect(), true, false);
        List<Option> options = new ArrayList<Option>();
        options.add(new Option("Option a", false, 1));
        options.add(new Option("Option b", false, 2));
        options.add(new Option("Option c", true, 3));
        options.add(new Option("Option d", false, 4));
        options.add(new Option("Option e", false, 5));

        exampleQuestion = new MultipleChoiceQuestion(EXAMPLE_QUESTION_TEXT, EXAMPLE_EXPLANATION_TEXT, options, true,
                false);
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowNPEIfOptionsListContainsNulls() throws Exception {
        List<Option> options = new ArrayList<Option>();
        options.add(null);
        new MultipleChoiceQuestion("Question", "Explanation", options, false, false);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfThereIsNoCorrectOptionInMultipleOptionMode() {
        new MultipleChoiceQuestion("Question", "Explanation", makeAllFalseOptions(), false, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfThereIsNoCorrectOptionInSingleOptionMode() {
        new MultipleChoiceQuestion("Question", "Explanation", makeAllFalseOptions(), false, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfThereAreManyCorrectOptionsInSingleOptionMode() {
        List<Option> options = makeTwoOptionsCorrectThreeIncorrect();
        new MultipleChoiceQuestion("Question", "Explanation", options, false, true);
    }

    private List<Option> makeTwoOptionsCorrectThreeIncorrect() {
        List<Option> options = new ArrayList<Option>();
        options.add(new Option("Option a", false, 1));
        options.add(new Option("Option b", true, 2));
        options.add(new Option("Option c", false, 3));
        options.add(new Option("Option d", true, 4));
        options.add(new Option("Option e", false, 5));
        return options;
    }

    private List<Option> makeAllFalseOptions() {
        List<Option> options = new ArrayList<Option>();
        options.add(new Option("Option a", false, 1));
        options.add(new Option("Option b", false, 2));
        options.add(new Option("Option c", false, 3));
        options.add(new Option("Option d", false, 4));
        options.add(new Option("Option e", false, 5));
        return options;
    }

    @Test
    public void shouldReturnCorrectNumberOfOptions() {
        assertEquals(2, multipleOptionModeQuestion.numberOfCorrectOptions());
    }

    @Test
    public void shouldThrowExceptionsIfNullsInConstructor() throws Exception {
        List<Option> options = makeTwoOptionsCorrectThreeIncorrect();
        try {
            new MultipleChoiceQuestion(null, "Foo", options, false, false);
            fail("Null in constructor didn't raise exception");
        } catch (IllegalArgumentException e) {
            // Exception expected from test
        }
        try {
            new MultipleChoiceQuestion("Foo", null, options, false, false);
            fail("Null in constructor didn't raise exception");
        } catch (IllegalArgumentException e) {
            // Exception expected from test
        }
        try {
            new MultipleChoiceQuestion("Foo", "Bar", null, false, false);
            fail("Null in constructor didn't raise exception");
        } catch (IllegalArgumentException e) {
            // Exception expected from test
        }

    }

}