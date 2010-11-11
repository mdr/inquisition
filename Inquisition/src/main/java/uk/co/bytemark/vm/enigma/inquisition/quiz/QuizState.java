package uk.co.bytemark.vm.enigma.inquisition.quiz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;
import uk.co.bytemark.vm.enigma.inquisition.questions.Answer;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;

public class QuizState implements Serializable {

    private static final Logger LOGGER = Logger.getLogger( QuizState.class.getName() );

    // Reference:

    private final QuizConfig quizConfig;

    private final List<Question> questionSequence;

    // Misc:

    private transient PropertyChangeSupport propertyChangeSupport;

    // State:

    private int currentQuestionIndex;

    // TODO: We should use indices here because there could be multiples of the same question
    private final Map<Question, Answer> answers;

    // TODO: We should use indices here because there could be multiples of the same question
    private final Set<Question> markedForReview;

    private boolean inExplanationMode;

    private boolean keepInExplanationModeWhileNavigating;

    private boolean timerRunning;

    private long startTime;

    private int secondsRemaining;

    private QuestionSet questionSet;

    private boolean dirty;

    public QuizState( QuestionSet questionSet, QuizConfig quizConfig ) {
        Utils.checkArgumentNotNull( questionSet, "questionSet" );
        Utils.checkArgumentNotNull( quizConfig, "quizConfig" );
        if ( questionSet.size() == 0 )
            throw new IllegalArgumentException( "Must have at least one question in an exam" );
        this.questionSet = questionSet;
        this.quizConfig = quizConfig;
        this.answers = new HashMap<Question, Answer>();
        this.questionSequence = getQuestionSequence( questionSet, quizConfig );
        for ( Question question : questionSequence )
            answers.put( question, question.initialAnswer() );
        this.currentQuestionIndex = 0;
        this.markedForReview = new HashSet<Question>();
        this.propertyChangeSupport = new PropertyChangeSupport( this );
        this.inExplanationMode = false;
        this.keepInExplanationModeWhileNavigating = false;
        this.timerRunning = false;
        this.startTime = -1;
        this.secondsRemaining = quizConfig.isQuizTimed() ? quizConfig.getTimeAllowed() : -1;
        this.dirty = false;
    }

    private List<Question> getQuestionSequence( QuestionSet questionSet_, QuizConfig quizConfig_ ) {
        List<Question> questions = new ArrayList<Question>( questionSet_.getQuestions() );
        if ( quizConfig_.shouldShuffleQuestionOrder() )
            Collections.shuffle( questions );
        return questions;
    }

    private void writeObject( ObjectOutputStream os ) throws Exception {
        try {
            os.defaultWriteObject();
        } catch ( Exception e ) {
            LOGGER.log( Level.SEVERE, "Exception serializing quiz state", e );
            throw e;
        }
    }

    private void readObject( ObjectInputStream is ) throws Exception {
        try {
            is.defaultReadObject();
            this.propertyChangeSupport = new PropertyChangeSupport( this );
        } catch ( Exception e ) {
            LOGGER.log( Level.SEVERE, "Exception deserializing quiz state", e );
            throw e;
        }
    }

    // -- Listeners ------------------------------------------------------------------------------------------------

    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        this.propertyChangeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
        this.propertyChangeSupport.removePropertyChangeListener( listener );
    }

    private void firePropertyChange() {
        this.propertyChangeSupport.firePropertyChange( new PropertyChangeEvent( this, null, null, null ) );
    }

    // -- Model updates --------------------------------------------------------------------------------------------

    public void goToFirstQuestion() {
        goToQuestionIndex( 0 );
    }

    public void goToNextQuestion() {
        if ( isLastQuestion() )
            throw new IllegalStateException( "Already at last question" );
        goToQuestionIndex( currentQuestionIndex + 1 );
    }

    public void goToPreviousQuestion() {
        if ( isFirstQuestion() )
            throw new IllegalStateException( "Already at last question" );
        goToQuestionIndex( currentQuestionIndex - 1 );
    }

    public void goToLastQuestion() {
        goToQuestionIndex( questionSequence.size() - 1 );
    }

    public void goToQuestion( int questionNumber ) {
        checkBounds( questionNumber );
        currentQuestionIndex = questionNumber - 1;
        goToQuestionIndex( questionNumber - 1 );
    }

    public void goToNextMarked() {
        if ( !isAnyQuestionMarked() )
            throw new IllegalStateException( "No question is marked" );

        for ( int i = 1; i <= questionSequence.size(); i++ ) {
            int index = ( currentQuestionIndex + i ) % questionSequence.size();
            Question question = questionSequence.get( index );
            if ( markedForReview.contains( question ) ) {
                goToQuestionIndex( index );
                return;
            }
        }
        throw new AssertionError( "Should never reach here" );
    }

    public void goToNextUnanswered() {
        if ( !isAnyQuestionUnanswered() )
            throw new IllegalStateException( "No question is unanswered" );

        for ( int i = 1; i <= questionSequence.size(); i++ ) {
            int index = ( currentQuestionIndex + i ) % questionSequence.size();
            Question question = questionSequence.get( index );
            if ( !isAnswered( question ) ) {
                goToQuestionIndex( index );
                return;
            }
        }
        throw new AssertionError( "Should never reach here" );
    }

    public void goToNextIncorrect() {
        if ( !isAnyQuestionIncorrect() )
            throw new IllegalStateException( "No question is incorrect" );

        for ( int i = 1; i <= questionSequence.size(); i++ ) {
            int index = ( currentQuestionIndex + i ) % questionSequence.size();
            Question question = questionSequence.get( index );
            if ( !isAnswered( question ) ) {
                goToQuestionIndex( index );
                return;
            }
        }
        throw new AssertionError( "Should never reach here" );
    }

    public void markCurrentQuestionForReview( boolean markQuestionForReview ) {
        int questionNumber = getQuestionNumber();
        markQuestionForReview( questionNumber, markQuestionForReview );
    }

    public void markQuestionForReview( int questionNumber, boolean markQuestionForReview ) {
        Question question = questionSequence.get( questionNumber - 1 );
        if ( markQuestionForReview ) {
            if ( markedForReview.contains( question ) )
                throw new IllegalStateException( "Question already marked for review" );
            else
                markedForReview.add( question );
        } else {
            if ( !markedForReview.contains( question ) )
                throw new IllegalStateException( "Question already not marked for review" );
            else
                markedForReview.remove( question );
        }
        dirty = true;
        firePropertyChange();
    }

    public void pauseTimer() {
        throwExceptionIfQuizIsNotTimed();
        if ( isTimerExpired() )
            throw new IllegalStateException( "Timer has expired" );
        if ( !isTimerRunning() )
            throw new IllegalStateException( "Timer is already paused" );
        secondsRemaining = getTimeRemaining();
        startTime = -1;
        timerRunning = false;
        firePropertyChange();
    }

    public void startTimer() {
        throwExceptionIfQuizIsNotTimed();
        if ( isTimerExpired() )
            throw new IllegalStateException( "Timer has expired" );
        if ( isTimerRunning() )
            throw new IllegalStateException( "Timer is already running" );
        timerRunning = true;
        startTime = System.currentTimeMillis();
        firePropertyChange();
    }

    public void setAnswer( Answer answer ) {
        // TODO: Check type?
        Question currentQuestion = getQuestion();
        answers.put( currentQuestion, answer );
        dirty = true;
        firePropertyChange();
    }

    public void setKeepInExplanationModeWhileNavigating( boolean keepInExplanationModeWhileNavigating ) {
        if ( this.keepInExplanationModeWhileNavigating == keepInExplanationModeWhileNavigating )
            throw new IllegalStateException();
        this.keepInExplanationModeWhileNavigating = keepInExplanationModeWhileNavigating;
        firePropertyChange();
    }

    public void toggleExplanationMode() {
        this.inExplanationMode = !inExplanationMode;
        firePropertyChange();
    }

    public void setNotDirty() {
        this.dirty = false;
    }

    // -- Model queries --------------------------------------------------------------------------------------------

    public boolean isLastQuestion() {
        return currentQuestionIndex == questionSequence.size() - 1;
    }

    public boolean isFirstQuestion() {
        return currentQuestionIndex == 0;
    }

    public int getQuestionNumber() {
        return currentQuestionIndex + 1;
    }

    public int getNumberOfQuestions() {
        return questionSequence.size();
    }

    public int getNumberOfAnsweredQuestions() {
        int count = 0;
        for ( Question question : questionSequence )
            if ( isAnswered( question ) )
                count++;
        return count;
    }

    public int getNumberOfCorrectQuestions() {
        int count = 0;
        for ( int questionNumber = 1; questionNumber <= getNumberOfQuestions(); questionNumber++ )
            if ( isCorrect( questionNumber ) )
                count++;
        return count;
    }

    public boolean isAnyQuestionIncorrect() {
        for ( Question question : questionSequence ) {
            Answer answer = answers.get( question );
            if ( !question.isCorrect( answer ) )
                return true;
        }
        return false;
    }

    public boolean isAnyQuestionUnanswered() {
        return getNumberOfAnsweredQuestions() != getNumberOfQuestions();
    }

    public boolean isAnyQuestionMarked() {
        return markedForReview.size() > 0;
    }

    public boolean shouldStateNumberOfOptionsNeededForMultipleChoice() {
        return quizConfig.shouldStateNumberOfOptionsNeededForMultipleChoice();
    }

    public Question getQuestion() {
        return questionSequence.get( currentQuestionIndex );
    }

    public Answer getAnswer() {
        return answers.get( getQuestion() );
    }

    public String getQuestionText() {
        return getQuestion().getSubstitutedQuestionText();
    }

    public String getExplanationText() {
        return getQuestion().getSubstitutedExplanationText();
    }

    public boolean isAnswered( int questionNumber ) {
        checkBounds( questionNumber );
        return isAnswered( questionSequence.get( questionNumber - 1 ) );
    }

    public String getQuestionType( int questionNumber ) {
        return questionSequence.get( questionNumber - 1 ).getQuestionTypeName();
    }

    public boolean isMarked( int questionNumber ) {
        checkBounds( questionNumber );
        return markedForReview.contains( getQuestion( questionNumber ) );
    }

    public boolean isMarked() {
        return isMarked( getQuestionNumber() );
    }

    public boolean isCorrect() {
        return isCorrect( getQuestionNumber() );
    }

    public boolean isCorrect( int questionNumber ) {
        checkBounds( questionNumber );
        Question question = getQuestion( questionNumber );
        Answer answer = answers.get( question );
        return question.isCorrect( answer );
    }

    public String questionType( int questionNumber ) {
        return getQuestion( questionNumber ).getQuestionTypeName();
    }

    public boolean isQuizTimed() {
        return quizConfig.isQuizTimed();
    }

    public boolean isTimerRunning() {
        throwExceptionIfQuizIsNotTimed();
        return timerRunning;
    }

    // In seconds
    public int getTimeRemaining() {
        throwExceptionIfQuizIsNotTimed();
        if ( timerRunning ) {
            long now = System.currentTimeMillis();
            int elapsedSeconds = (int) ( ( now - startTime ) / 1000 );
            return secondsRemaining - elapsedSeconds;
        } else {
            return secondsRemaining;
        }
    }

    public boolean isTimerExpired() {
        throwExceptionIfQuizIsNotTimed();
        return getTimeRemaining() < 0;
    }

    public boolean isInExplanationMode() {
        return inExplanationMode;
    }

    public boolean keepInExplanationModeWhileNavigating() {
        return keepInExplanationModeWhileNavigating;
    }

    public String getQuestionSetName() {
        return questionSet.getName();
    }

    public boolean isAnyQuestionAnswered() {
        return getNumberOfAnsweredQuestions() > 0;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    // -- Helper ---------------------------------------------------------------

    private Question getQuestion( int questionNumber ) {
        return questionSequence.get( questionNumber - 1 );
    }

    private boolean isAnswered( Question question ) {
        Answer answer = answers.get( question );
        boolean answered;
        if ( question instanceof MultipleChoiceQuestion ) {
            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
            boolean numberOfOptionsNeededIsShown = shouldStateNumberOfOptionsNeededForMultipleChoice();
            answered = multipleChoiceQuestion.isAnswered( answer, numberOfOptionsNeededIsShown );
        } else if ( question instanceof DragAndDropQuestion ) {
            DragAndDropQuestion dragAndDropQuestion = (DragAndDropQuestion) question;
            answered = dragAndDropQuestion.isAnswered( answer );
        } else {
            throw new AssertionError( "Unknown Question type: " + question );
        }
        return answered;
    }

    private void checkBounds( int questionNumber ) {
        if ( ! ( questionNumber >= 1 && questionNumber <= questionSequence.size() ) )
            throw new IllegalArgumentException( "No such question: " + questionNumber );
    }

    private void throwExceptionIfQuizIsNotTimed() {
        if ( !isQuizTimed() )
            throw new IllegalArgumentException( "Quiz is not timed" );
    }

    private void updateExplanationMode() {
        if ( inExplanationMode && !keepInExplanationModeWhileNavigating )
            inExplanationMode = false;
    }

    private void goToQuestionIndex( int questionIndex ) {
        currentQuestionIndex = questionIndex;
        updateExplanationMode();
        firePropertyChange();
    }

}
