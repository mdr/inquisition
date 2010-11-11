package uk.co.bytemark.vm.enigma.inquisition.quiz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion.Builder;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet.QuestionSetBuilder;

public class QuizStateNavigationTest {

    private QuizState              quizState;
    private MultipleChoiceQuestion question1;
    private MultipleChoiceQuestion question2;
    private MultipleChoiceQuestion question3;
    private MultipleChoiceQuestion question4;
    private Map<Integer, Question> questionsMap;

    @Test
    public void initialStateOfQuiz() throws Exception {
        assertEquals(question1, quizState.getQuestion());
        assertEquals(0, quizState.getNumberOfAnsweredQuestions());
        assertEquals(4, quizState.getNumberOfQuestions());
        assertEquals(1, quizState.getQuestionNumber());
        assertFalse(quizState.isAnyQuestionMarked());
        assertTrue(quizState.isAnyQuestionUnanswered());
        assertTrue(quizState.isFirstQuestion());
        assertFalse(quizState.isQuizTimed());
        assertEquals(question1.getSubstitutedExplanationText(), quizState.getExplanationText());
        for (int questionNumber = 1; questionNumber <= quizState.getNumberOfQuestions(); questionNumber++) {
            assertFalse(quizState.isCorrect(questionNumber));
            assertFalse(quizState.isMarked(questionNumber));
            assertFalse(quizState.isAnswered(questionNumber));
        }
    }

    @Test
    public void basicNavigation() throws Exception {
        checkCurrentLocation(1);
        quizState.goToNextQuestion();
        checkCurrentLocation(2);
        assertFalse(quizState.isFirstQuestion());
        assertFalse(quizState.isLastQuestion());
        quizState.goToNextQuestion();
        checkCurrentLocation(3);
        quizState.goToNextQuestion();
        checkCurrentLocation(4);

        quizState.goToPreviousQuestion();
        checkCurrentLocation(3);
        quizState.goToPreviousQuestion();
        checkCurrentLocation(2);
        quizState.goToPreviousQuestion();
        checkCurrentLocation(1);

        quizState.goToLastQuestion();
        assertTrue(quizState.isLastQuestion());
        checkCurrentLocation(4);

        quizState.goToFirstQuestion();
        assertTrue(quizState.isFirstQuestion());
        checkCurrentLocation(1);

        for (int questionNumber = 1; questionNumber <= quizState.getNumberOfQuestions(); questionNumber++) {
            quizState.goToQuestion(questionNumber);
            assertEquals(questionNumber, quizState.getQuestionNumber());
        }
    }

    @Test
    public void marked() throws Exception {
        assertMarked();
        quizState.markCurrentQuestionForReview(true);
        assertMarked(1);
        quizState.goToLastQuestion();
        quizState.markCurrentQuestionForReview(true);
        assertMarked(1, 4);
        quizState.goToFirstQuestion();
        quizState.markCurrentQuestionForReview(false);
        assertMarked(4);
        quizState.goToQuestion(2);
        quizState.markCurrentQuestionForReview(true);
        assertMarked(2, 4);

        quizState.goToQuestion(3);
        quizState.goToNextMarked();
        checkCurrentLocation(4);
        quizState.goToNextMarked();
        checkCurrentLocation(2);
        quizState.goToNextMarked();
        checkCurrentLocation(4);
    }

    private void assertMarked(Integer... questionNumbersArray) {
        Set<Integer> expectedMarkedQuestionNumbers = Utils.makeSet(questionNumbersArray);
        assertEquals(expectedMarkedQuestionNumbers.size() > 0, quizState.isAnyQuestionMarked());
        Set<Integer> actualMarkedQuestionNumbers = new HashSet<Integer>();
        for (int questionNumber = 1; questionNumber <= quizState.getNumberOfQuestions(); questionNumber++)
            if (quizState.isMarked(questionNumber))
                actualMarkedQuestionNumbers.add(questionNumber);
        assertEquals(expectedMarkedQuestionNumbers, actualMarkedQuestionNumbers);
    }

    private void checkCurrentLocation(int expectedCurrentQuestionNumber) {
        assertEquals(questionsMap.get(expectedCurrentQuestionNumber), quizState.getQuestion());
        assertEquals(expectedCurrentQuestionNumber, quizState.getQuestionNumber());
    }

    private MultipleChoiceQuestion makeMultipleChoiceQuestion(int questionNumber) {
        return new Builder().questionText("Question " + questionNumber).explanationText("Explanation").option("First",
                true).build();
    }

    @Before
    public void setUp() throws Exception {
        QuizConfig quizConfig = QuizConfig.createWithoutTimer(shuffleQuestions(false), stateNumberOfOptions(false));
        question1 = makeMultipleChoiceQuestion(1);
        question2 = makeMultipleChoiceQuestion(2);
        question3 = makeMultipleChoiceQuestion(3);
        question4 = makeMultipleChoiceQuestion(4);
        QuestionSet questionSet = new QuestionSetBuilder().name("QuestionSet1").addQuestions(question1, question2,
                question3, question4).build();
        quizState = new QuizState(questionSet, quizConfig);
        questionsMap = new HashMap<Integer, Question>();
        questionsMap.put(1, question1);
        questionsMap.put(2, question2);
        questionsMap.put(3, question3);
        questionsMap.put(4, question4);
    }

    // -- Argument-documenting methods ----------------------------------------------------------------------

    private boolean shuffleQuestions(boolean in) {
        return in;
    }

    private boolean stateNumberOfOptions(boolean in) {
        return in;
    }

}
