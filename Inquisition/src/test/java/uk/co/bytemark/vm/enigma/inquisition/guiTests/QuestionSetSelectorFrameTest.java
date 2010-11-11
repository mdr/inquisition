package uk.co.bytemark.vm.enigma.inquisition.guiTests;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uispec4j.TextBox;
import org.uispec4j.Tree;
import org.uispec4j.UISpec4J;
import org.uispec4j.UISpecAdapter;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;

import uk.co.bytemark.vm.enigma.inquisition.gui.misc.SwingComponentNames;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.MockQuestionSetMother;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.QuestionSetSelectorFrame;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;

// On Linux, use -Dawt.toolkit=sun.awt.motif.MToolkit

public class QuestionSetSelectorFrameTest extends UISpecTestCase {


    @Override
    @Before
    public void setUp() throws Exception {
        final QuestionSetSelectorFrame quizSelectorFrame = getMockQuestionSetSelectorFrame();
        setAdapter(new UISpecAdapter() {
            public Window getMainWindow() {
                return new Window(quizSelectorFrame);
            }

        });
    }

    private QuestionSetSelectorFrame getMockQuestionSetSelectorFrame() {
        List<QuestionSet> questionSets =MockQuestionSetMother.getInitialQuestionSets();
        final QuestionSetSelectorFrame quizSelectorFrame = new QuestionSetSelectorFrame(questionSets);
        return quizSelectorFrame;
    }

    static {
        UISpec4J.init();
    }



    @Test
    public void testInitialState() throws Exception {
        Window window = getMainWindow();

        checkControlsAreDisabled(window);

        TextBox numberOfQuestionsTextBox = window.getTextBox(SwingComponentNames.NUMBER_OF_QUESTIONS_TEXT);
        assertFalse(numberOfQuestionsTextBox.isEditable());
        TextBox descriptionTextBox = window.getTextBox(SwingComponentNames.DESCRIPTION_TEXT);
        assertFalse(descriptionTextBox.isEditable());
        assertFalse(window.getButton(SwingComponentNames.EDIT_QUESTIONS_BUTTON).isVisible());

        Tree questionSetTree = window.getTree();
        assertTrue(questionSetTree.selectionIsEmpty());

        questionSetTree.contentEquals(SwingComponentNames.BEGIN_BUTTON);
        assertTrue(questionSetTree.contentEquals("" + //
                "Root\n" + //
                "  Mock Package 1\n" + //
                "    Mock Questions 1\n" + //
                "    Mock Questions 2\n" + //
                "  Mock Package 2\n" + // 
                "    Mock Questions 3\n" + //
                "  Other\n" + // 
                "    Mock Questions 4\n" //
        ));

        // Tree should be fully expanded
        questionSetTree.pathIsExpanded("Root");
        questionSetTree.pathIsExpanded("Root/Mock Package 1");
        questionSetTree.pathIsExpanded("Root/Mock Package 2");
        questionSetTree.pathIsExpanded("Root/Other");
    }

    public void testEnableAndDisableOfGUI() throws Exception {
        Window window = getMainWindow();
        Tree questionSetTree = window.getTree();
        checkControlsAreDisabled(window);
        questionSetTree.select("Mock Package 1/Mock Questions 1");
        checkControlsAreEnabled(window);
        questionSetTree.select("Mock Package 1");
        checkControlsAreDisabled(window);
        questionSetTree.select("Mock Package 2/Mock Questions 3");
        checkControlsAreEnabled(window);
        questionSetTree.select("Other/Mock Questions 4");
        checkControlsAreEnabled(window);
        questionSetTree.select("");
        checkControlsAreDisabled(window);
    }

    public void testDisplayOfSingleQuestionSet() throws Exception {
        Window window = getMainWindow();
        Tree questionSetTree = window.getTree();
        questionSetTree.select("Mock Package 1/Mock Questions 1");
        TextBox numberOfQuestionsTextBox = window.getTextBox(SwingComponentNames.NUMBER_OF_QUESTIONS_TEXT);
        assertEquals("1 (1 multiple choice, 0 drag and drop)", numberOfQuestionsTextBox.getText());
        TextBox descriptionTextBox = window.getTextBox(SwingComponentNames.DESCRIPTION_TEXT);
        assertTrue(descriptionTextBox.getText().contains(MockQuestionSetMother.QUESTION_SET_DESCRIPTION));

    }

    public void testSelectQuestionSetAndBegin() throws Exception {
        Window window = getMainWindow();
        Tree questionSetTree = window.getTree();
        questionSetTree.select("Mock Package 1/Mock Questions 1");
        Window examWindow = WindowInterceptor.run(window.getButton(SwingComponentNames.BEGIN_BUTTON).triggerClick());
        assertFalse(window.isVisible());
        assertTrue(examWindow.isVisible());
    }

    private void checkControlsAreDisabled(Window window) {
        TextBox numberOfQuestionsTextBox = window.getTextBox(SwingComponentNames.NUMBER_OF_QUESTIONS_TEXT);
        assertEquals("", numberOfQuestionsTextBox.getText());
        assertFalse(numberOfQuestionsTextBox.isEnabled());
        TextBox descriptionTextBox = window.getTextBox(SwingComponentNames.DESCRIPTION_TEXT);
        assertFalse(descriptionTextBox.isEnabled());
        assertFalse(window.getButton(SwingComponentNames.BEGIN_BUTTON).isEnabled());
        assertFalse(window.getButton(SwingComponentNames.EDIT_QUESTIONS_BUTTON).isEnabled());
        assertFalse(window.getCheckBox("shuffleCheckBox").isEnabled());
        assertFalse(window.getCheckBox("stateOptionsCheckBox").isEnabled());
        assertFalse(window.getCheckBox("useTimerCheckBox").isEnabled());
        assertFalse(window.getSlider("timeSlider").isEnabled());
    }

    private void checkControlsAreEnabled(Window window) {
        TextBox numberOfQuestionsTextBox = window.getTextBox(SwingComponentNames.NUMBER_OF_QUESTIONS_TEXT);
        assertFalse(numberOfQuestionsTextBox.getText().equals(""));
        assertTrue(numberOfQuestionsTextBox.isEnabled());
        TextBox descriptionTextBox = window.getTextBox(SwingComponentNames.DESCRIPTION_TEXT);
        assertTrue(descriptionTextBox.isEnabled());
        assertTrue(window.getButton(SwingComponentNames.BEGIN_BUTTON).isEnabled());
        assertTrue(window.getButton(SwingComponentNames.EDIT_QUESTIONS_BUTTON).isEnabled());
        assertTrue(window.getCheckBox("shuffleCheckBox").isEnabled());
        assertTrue(window.getCheckBox("stateOptionsCheckBox").isEnabled());
        assertTrue(window.getCheckBox("useTimerCheckBox").isEnabled());
        assertTrue(window.getSlider("timeSlider").isEnabled());
    }

}
