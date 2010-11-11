package uk.co.bytemark.vm.enigma.inquisition.guiTests;

import java.util.List;

import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.MockQuestionSetMother;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.QuestionSetSelectorFrame;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;

public class QuizStateNavigationTest {

    private FrameFixture window;

    @Test
    public void initialStateOfQuiz() throws Exception {
    // Cannot instantiate class
    }

    @Before
    public void setUp() throws Exception {
        List<QuestionSet> questionSets = MockQuestionSetMother.getInitialQuestionSets();
        QuestionSetSelectorFrame quizSelectorFrame = new QuestionSetSelectorFrame(questionSets);
        window = new FrameFixture(quizSelectorFrame);
        window.show();
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }
}
