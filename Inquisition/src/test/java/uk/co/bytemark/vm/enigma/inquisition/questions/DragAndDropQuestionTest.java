package uk.co.bytemark.vm.enigma.inquisition.questions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class DragAndDropQuestionTest {

    @Test
    public void toXmlAndBackAgainShouldProduceSameQuestion() throws Exception {
        List<String> extraFragments = Arrays.asList("Wibble", "Wobble");
        String questionText = "Question <slot>foo</slot> more question <slot>bar</slot>";
        String explanationText = "Explanation <slot>foo</slot> more question <slot>bar</slot>";
        boolean reuseFragments = false;
        DragAndDropQuestion dragAndDropQuestion = new DragAndDropQuestion(questionText, explanationText,
                extraFragments, reuseFragments);

        assertEquals(dragAndDropQuestion, new DragAndDropQuestion(dragAndDropQuestion.asXML()));
    }

    @Test
    public void shouldReturnCorrectFragmentsBasic() throws Exception {
        String questionText1 = "Foo <slot>bar</slot> baz <slot>wibble wobble</slot> foo <slot>boz</slot>";
        checkReturnsCorrectFragments(questionText1, false, "bar", "wibble wobble", "boz");

        String questionText2 = "<slot>bar</SLOT> baz <sLOt>wibble wobble</SloT> foo <SlOT>boz</SLoT>";
        checkReturnsCorrectFragments(questionText2, false, "bar", "wibble wobble", "boz");

        String questionText3 = "wibble wobble";
        checkReturnsCorrectFragments(questionText3, false);
    }

    @Test
    public void shouldReturnCorrectFragmentsWhenDuplicatesExist() throws Exception {
        String questionText;
        questionText = "<slot>foo</slot> <slot>bar</slot> <slot>foo</slot> <slot>bar</slot>";
        checkReturnsCorrectFragments(questionText, false, "foo", "bar", "foo", "bar");

        questionText = "<slot>foo</slot> <slot>bar</slot> <slot>foo</slot> <slot>bar</slot>";
        checkReturnsCorrectFragments(questionText, true, "foo", "bar");

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNestedSlotsOccur() throws Exception {
        String questionText = "<slot> foo <slot>bar</slot> foo </slot>";
        new DragAndDropQuestion(questionText, "Explanation", new ArrayList<String>(), true);
    }

    private void checkReturnsCorrectFragments(String questionText, boolean reuseFragments, String... expectedFragments) {
        DragAndDropQuestion question = new DragAndDropQuestion(questionText, "Explanation", Collections
                .<String> emptyList(), reuseFragments);

        List<String> actualFragments = question.getFragments();
        assertNotNull(actualFragments);
        assertEquals(expectedFragments.length, actualFragments.size());
        List<String> expectedFragmentsList = Arrays.asList(expectedFragments);
        assertTrue(actualFragments.containsAll(expectedFragmentsList));
        assertTrue(expectedFragmentsList.containsAll(actualFragments));
    }

    @Test(expected = NullPointerException.class)
    public void constructorShouldThrowNPEIfExtraFragmentsContainsNulls() throws Exception {
        List<String> extraFragments = Arrays.asList("Foo", null, "Bar");
        new DragAndDropQuestion("Question", "Explanation", extraFragments, false);
    }

    @Test
    public void shouldThrowExceptionsIfNullsInConstructor() throws Exception {
        try {
            new DragAndDropQuestion(null, "Explanation", Collections.<String> emptyList(), true);
            fail("Null in constructor didn't raise exception");
        } catch (IllegalArgumentException e) {
            // Exception expected from test
        }
        try {
            new DragAndDropQuestion("Question", null, Collections.<String> emptyList(), true);
            fail("Null in constructor didn't raise exception");
        } catch (IllegalArgumentException e) {
            // Exception expected from test
        }
        try {
            new DragAndDropQuestion("Question", "Explanation", null, true);
            fail("Null in constructor didn't raise exception");
        } catch (IllegalArgumentException e) {
            // Exception expected from test
        }
        try {
            new DragAndDropQuestion(null);
            fail("Null in constructor didn't raise exception");
        } catch (NullPointerException e) {
            // Exception expected from test
        }

    }

    @Test
    public void dollarSignsInCopyToExplanationShouldNotDie() throws Exception {
        DragAndDropQuestion question = new DragAndDropQuestion("<CopyToExplanation>${foo}</CopyToExplanation>",
                "<CopyFromQuestion/>", Collections.<String> emptyList(), true);
        String substitutedQuestionText = question.getSubstitutedExplanationText();
        assertEquals("${foo}", substitutedQuestionText);
    }

}
