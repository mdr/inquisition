package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import uk.co.bytemark.vm.enigma.inquisition.gui.images.Icons;
import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.InquisitionMain;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.ReturnCallback;
import uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser.SuffixFileFilter;
import uk.co.bytemark.vm.enigma.inquisition.gui.screens.AboutDialog;
import uk.co.bytemark.vm.enigma.inquisition.gui.screens.AbstractQuizFrame;
import uk.co.bytemark.vm.enigma.inquisition.misc.Constants;
import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;
import uk.co.bytemark.vm.enigma.inquisition.questions.Answer;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSetManager;
import uk.co.bytemark.vm.enigma.inquisition.quiz.QuizState;

public class QuizFrame extends AbstractQuizFrame implements PropertyChangeListener {

    // Only touch quizState from the Swing thread
    private final QuizState quizState;

    private final QuestionPanelManager questionPanelManager;

    private final TransparentPane glassPane;

    private final ReturnCallback returnCallback;

    private boolean updating = false;

    private boolean showingExplanation = false;

    private String currentExplanation = null;

    private int previousSplitSize = -1;

    private QuizTimerThread quizTimerThread;

    public QuizFrame(final QuizState quizState, ReturnCallback returnCallback) {
        Utils.checkArgumentNotNull(quizState, "quizState");
        Utils.checkArgumentNotNull(returnCallback, "returnCallback");
        this.returnCallback = returnCallback;
        this.quizState = quizState;
        this.questionPanelManager = new QuestionPanelManager();

        initializeGuiFurther();

        glassPane = new TransparentPane();
        setGlassPane(glassPane);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addEventHandlers();

        if (quizState.isQuizTimed())
            quizState.startTimer();
        quizState.addPropertyChangeListener(this);

        launchQuizTimer();
        updateFromModel();

    }

    private void launchQuizTimer() {
        quizTimerThread = new QuizTimerThread();
        quizTimerThread.start();
    }

    private void initializeGuiFurther() {
        // Add a favicon thingy
        if (Icons.FAVICON.isAvailable())
            setIconImage(Icons.FAVICON.getImage());

        setTitle("Inquisition - " + quizState.getQuestionSetName());

        // Respond dynamically to window resizes
        Toolkit.getDefaultToolkit().setDynamicLayout(true);

        if (Icons.FIRST.isAvailable())
            getFirstQuestionButton().setIcon(Icons.FIRST.getIcon());
        if (Icons.BACK.isAvailable())
            getPreviousQuestionButton().setIcon(Icons.BACK.getIcon());
        if (Icons.FORWARD.isAvailable())
            getNextQuestionButton().setIcon(Icons.FORWARD.getIcon());
        if (Icons.LAST.isAvailable())
            getLastQuestionButton().setIcon(Icons.LAST.getIcon());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        getExplanationTextPane().setEditorKit(new ExplanationPanelHTMLEditorKit());
    }

    private void updateFromModel() {
        if (updating)
            throw new IllegalStateException("Attempting to update while already updating");
        updating = true;
        try {
            updateQuestionNavigationComboBox();
            getFirstQuestionButton().setEnabled(!quizState.isFirstQuestion());
            getPreviousQuestionButton().setEnabled(!quizState.isFirstQuestion());
            getNextQuestionButton().setEnabled(!quizState.isLastQuestion());
            getLastQuestionButton().setEnabled(!quizState.isLastQuestion());
            getFindNextMarkedButton().setEnabled(quizState.isAnyQuestionMarked());
            getFindNextNextUnansweredButton().setEnabled(quizState.isAnyQuestionUnanswered());
            getMarkedForReviewBox().setSelected(quizState.isMarked());
            updateProgressBar();
            updateTimerDisplay();
            updateQuestion();
            updateExplanationPanel();
        } finally {
            updating = false;
        }
    }

    private void updateExplanationPanel() {
        if (quizState.isInExplanationMode()) {
            setRightWrongLabels();
            // getCurrentQuestionPanel().enterReviewMode();
            if (!showingExplanation) {
                getExplanationPanel().setVisible(true);
                getCheckAnswerButton().setText("Hide answer and explanation");
                if (previousSplitSize == -1)
                    getSplitPane().setDividerLocation(-1);
                else
                    getSplitPane().setDividerLocation(previousSplitSize);

                getSplitPane().setDividerSize(5);
                showingExplanation = true;
            }
            String explanationText = quizState.getExplanationText();
            if (!explanationText.equals(currentExplanation)) {
                String explanationText2 = questionPanelManager.getExplanationText(quizState.getQuestionNumber());
                // getExplanationTextPane().setText(explanationText); // TODO: Fix this
                getExplanationTextPane().setText(explanationText2);
                getExplanationTextPane().setCaretPosition(0);
                currentExplanation = explanationText;
            }

        } else {
            getExplanationPanel().setVisible(false);
            getCheckAnswerButton().setText("Show answer and explanation");
            if (showingExplanation)
                previousSplitSize = getSplitPane().getDividerLocation();
            getSplitPane().setDividerLocation(1.0D);
            getSplitPane().setDividerSize(0);
            showingExplanation = false;
        }
        getPinExplanationPanelCheckBox().setSelected(quizState.keepInExplanationModeWhileNavigating());
        getFindNextIncorrectAnswerButton().setEnabled(quizState.isAnyQuestionIncorrect());
    }

    private void setRightWrongLabels() {
        JLabel rightOrWrongLabel = getRightOrWrongLabel();
        if (quizState.isCorrect()) {
            if (Icons.TICK.isAvailable())
                rightOrWrongLabel.setIcon(Icons.TICK.getIcon());
            rightOrWrongLabel.setText("Correct");
        } else {
            if (Icons.CROSS.isAvailable())
                rightOrWrongLabel.setIcon(Icons.CROSS.getIcon());
            rightOrWrongLabel.setText("Incorrect");
        }
    }

    private void updateQuestion() {
        final int questionNumber = quizState.getQuestionNumber();
        Question question = quizState.getQuestion();
        if (!questionPanelManager.hasPanelFor(questionNumber)
                || !questionPanelManager.isPanelCorrectForQuestion(questionNumber, question)) {
            questionPanelManager.createQuestionPanel(this, questionNumber, question);
        }
        questionPanelManager.setAnswer(questionNumber, quizState.getAnswer());
        questionPanelManager.showPanel(questionNumber);

        questionPanelManager.setExplanationMode(questionNumber, quizState.isInExplanationMode());
        questionPanelManager.fixLayout(questionNumber);
    }

    private void updateTimerDisplay() {
        boolean isQuizTimed = quizState.isQuizTimed();
        if (isQuizTimed) {
            if (quizState.isTimerExpired()) {
                getTimeRemainingTextField().setText("Time's up!");
                getPauseButton().setVisible(false);
            } else {
                int timeRemaining = quizState.getTimeRemaining();
                getTimeRemainingTextField().setText(formatTimeRemaining(timeRemaining));
                getPauseButton().setVisible(true);
            }
            if (quizState.isTimerRunning()) {
                getPauseButton().setText("Pause");
            } else {
                getPauseButton().setText("Restart");
            }
        } else {
            getPauseButton().setVisible(false);
        }
        getTimeRemainingTextField().setVisible(isQuizTimed);
        getTimeRemainingLabel().setVisible(isQuizTimed);
    }

    private String formatTimeRemaining(int timeRemaining) {
        int myTimeRemaining = timeRemaining;
        final int hours = myTimeRemaining / (60 * 60);
        myTimeRemaining -= hours * 60 * 60;
        final int minutes = myTimeRemaining / 60;
        final int seconds = myTimeRemaining - minutes * 60;
        if (hours == 0)
            return String.format("%02d:%02d", minutes, seconds);
        else
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updateProgressBar() {
        int questionsAnswered = quizState.getNumberOfAnsweredQuestions();
        int numberOfQuestions = quizState.getNumberOfQuestions();
        double percentage = 100.0 * questionsAnswered / numberOfQuestions;
        String percentageString = String.format(" (%.0f%%)", percentage);
        getAnsweredProgressBar().setString(questionsAnswered + " / " + numberOfQuestions + percentageString);
        getAnsweredProgressBar().setValue((int) percentage);
    }

    private void updateQuestionNavigationComboBox() {
        JComboBox navigateToQuestionComboBox = getNavigateToQuestionComboBox();
        int numberOfQuestions = quizState.getNumberOfQuestions();
        int previousSize = navigateToQuestionComboBox.getModel().getSize();
        if (numberOfQuestions != previousSize) {
            String[] questionNumbers = new String[numberOfQuestions];
            for (int i = 0; i < numberOfQuestions; i++)
                questionNumbers[i] = Integer.toString(i + 1);
            navigateToQuestionComboBox.setModel(new DefaultComboBoxModel(questionNumbers));
        }
        navigateToQuestionComboBox.setSelectedIndex(quizState.getQuestionNumber() - 1);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateFromModel();
    }

    private void addEventHandlers() {
        getFirstQuestionButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.goToFirstQuestion();
            }
        });
        getPreviousQuestionButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.goToPreviousQuestion();
            }
        });
        getNextQuestionButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.goToNextQuestion();
            }
        });
        getLastQuestionButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.goToLastQuestion();
            }
        });
        getNavigateToQuestionComboBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (updating)
                    return;
                int questionNumber = getNavigateToQuestionComboBox().getSelectedIndex() + 1;
                quizState.goToQuestion(questionNumber);
            }
        });
        getFindNextIncorrectAnswerButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.goToNextIncorrect();
            }
        });
        getFindNextMarkedButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.goToNextMarked();
            }
        });
        getFindNextNextUnansweredButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.goToNextUnanswered();
            }
        });
        getMarkedForReviewBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.markCurrentQuestionForReview(getMarkedForReviewBox().isSelected());
            }
        });
        getPauseButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (quizState.isTimerRunning())
                    quizState.pauseTimer();
                else
                    quizState.startTimer();
            }
        });
        getCheckAnswerButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.toggleExplanationMode();
            }
        });
        getPinExplanationPanelCheckBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quizState.setKeepInExplanationModeWhileNavigating(getPinExplanationPanelCheckBox().isSelected());
            }
        });
        getOverviewButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!quizState.isInExplanationMode())
                    quizState.toggleExplanationMode();
                if (!quizState.keepInExplanationModeWhileNavigating())
                    quizState.setKeepInExplanationModeWhileNavigating(true);
                ResultsDialog resultsDialog = new ResultsDialog(QuizFrame.this, quizState);
                resultsDialog.setLocationRelativeTo(QuizFrame.this);
                resultsDialog.setVisible(true);
            }
        });
        getSaveQuizMenuItem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                boolean successful = doSaveQuizStateProtocol();
                if (successful)
                    endQuiz();
            }

        });
        getExitQuizMenuItem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doExitQuizProtocol();
            }
        });
        getAboutMenuItem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JDialog aboutDialog = new AboutDialog(QuizFrame.this, Constants.getAboutText());
                aboutDialog.setLocationRelativeTo(QuizFrame.this);
                aboutDialog.setVisible(true);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                doExitQuizProtocol();
            }
        });
        addComponentListener(new ResizeListener());

    }

    private boolean doSaveQuizStateProtocol() {
        boolean resumeTimerIfCancelled;
        if (quizState.isQuizTimed() && quizState.isTimerRunning()) {
            quizState.pauseTimer();
            resumeTimerIfCancelled = true;
        } else {
            resumeTimerIfCancelled = false;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save In-Progress Quiz");
        SuffixFileFilter.setSoleFileFilter(fileChooser, Constants.QUIZ_SUFFIX_FILE_FILTER);
        int resultStatus = fileChooser.showSaveDialog(QuizFrame.this);
        if (resultStatus != JFileChooser.APPROVE_OPTION) {
            if (resumeTimerIfCancelled)
                quizState.startTimer();
            return false;
        }
        String quizStateFileName = fileChooser.getSelectedFile().getAbsolutePath();
        File quizStateFile = new File(quizStateFileName);
        if (!Constants.QUIZ_SUFFIX_FILE_FILTER.accept(quizStateFile)) {
            quizStateFileName = Constants.QUIZ_SUFFIX_FILE_FILTER.addSuffixTo(quizStateFileName);
            quizStateFile = new File(quizStateFileName);
        }
        if (quizStateFile.exists()) {
            int confirmStatus = JOptionPane.showConfirmDialog(QuizFrame.this, "'" + quizStateFile.getAbsolutePath()
                    + "' exists. Do you want to overwrite it?", "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION);
            if (confirmStatus != JOptionPane.OK_OPTION) {
                if (resumeTimerIfCancelled)
                    quizState.startTimer();
                return false;
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(quizStateFileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(quizState);
            objectOutputStream.close();
            quizState.setNotDirty();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(QuizFrame.this, "Could not save quiz '" + quizStateFileName
                    + "' due to error '" + e.getMessage() + "'", "Could not save quiz", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void setGlassPaneVisible(boolean visible) {
        glassPane.setVisible(visible);
    }

    private class QuizTimerThread extends Thread {

        private boolean keepRunning = true;

        @Override
        public void run() {
            while (keepRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Don't care
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateTimerDisplay();
                    }
                });
            }
        }

        public void finish() {
            if (!keepRunning)
                throw new IllegalStateException("Not running");
            keepRunning = false;
            interrupt();
        }

    }

    private void endQuiz() {
        setVisible(false);
        quizTimerThread.finish();
        dispose();
        returnCallback.doReturn();
    }

    private void doExitQuizProtocol() {
        if (quizState.isDirty()) {
            int saveStatus = JOptionPane.showConfirmDialog(QuizFrame.this, "Do you want to save the current quiz?",
                    "End Quiz", JOptionPane.YES_NO_CANCEL_OPTION);
            if (saveStatus == JOptionPane.CANCEL_OPTION)
                return;
            else if (saveStatus == JOptionPane.YES_OPTION) {
                boolean successful = doSaveQuizStateProtocol();
                if (!successful)
                    return;
            }
        }
        endQuiz();
    }

    private class QuestionPanelManager {

        private Map<Integer, QuestionPanel> questionPanels = new HashMap<Integer, QuestionPanel>();

        private Map<Integer, Question> questions = new HashMap<Integer, Question>();

        public boolean hasPanelFor(int questionNumber) {
            return questionPanels.containsKey(questionNumber);
        }

        public void setAnswer(int questionNumber, Answer answer) {
            questionPanels.get(questionNumber).setAnswer(answer);
        }

        // TODO: Don't want this
        public String getExplanationText(int questionNumber) {
            return questionPanels.get(questionNumber).getExplanationText();
        }

        protected boolean isPanelCorrectForQuestion(int questionNumber, Question question) {
            return questions.containsKey(questionNumber);
        }

        public void fixLayout(int questionNumber) {
            QuestionPanel questionPanel = questionPanels.get(questionNumber);
            if (questionPanel instanceof MultipleChoicePanel)
                ((MultipleChoicePanel) questionPanel).fixDividerLocation();
        }

        public void showPanel(int questionNumber) {
            JPanel questionPanelHolderPanel = getQuestionPanelHolderPanel();
            CardLayout questionPanelLayout = (CardLayout) questionPanelHolderPanel.getLayout();
            questionPanelLayout.show(questionPanelHolderPanel, Integer.toString(questionNumber));

        }

        public void setPanel(int questionNumber, QuestionPanel panel, Question question) {
            JPanel questionPanelHolderPanel = getQuestionPanelHolderPanel();
            questionPanelHolderPanel.add(panel, Integer.toString(questionNumber));
            questionPanels.put(questionNumber, panel);
            questions.put(questionNumber, question);
        }

        public void setExplanationMode(int questionNumber, boolean inExplanationMode) {
            QuestionPanel questionPanel = questionPanels.get(questionNumber);
            if (inExplanationMode)
                questionPanel.enterReviewMode();
            else
                questionPanel.enterQuestionMode();
        }

        public void createQuestionPanel(final QuizFrame quizFrame, final int questionNumber, Question question)
                throws AssertionError {
            AnswerChangedObserver answerChangedObserver = new AnswerChangedObserver() {
                public void answerChanged(Answer answer) {
                    if (quizFrame.quizState.getQuestionNumber() != questionNumber)
                        throw new IllegalStateException("Should not be updating answer of non-current question");
                    quizFrame.quizState.setAnswer(answer);
                }
            };
            QuestionPanel panel;
            if (question instanceof MultipleChoiceQuestion) {
                panel = new MultipleChoicePanel((MultipleChoiceQuestion) question, answerChangedObserver,
                        quizFrame.quizState.shouldStateNumberOfOptionsNeededForMultipleChoice());
            } else if (question instanceof DragAndDropQuestion) {
                panel = new DragAndDropPanel((DragAndDropQuestion) question, quizFrame, answerChangedObserver,
                        quizFrame.glassPane);
            } else {
                throw new AssertionError("Unknown question type " + question.getClass());
            }
            setPanel(questionNumber, panel, question);
        }

    }

    public static void main(String[] args) {

        InquisitionMain.setupLookAndFeel();
        QuizConfig quizConfig = QuizConfig.createWithTimer(false, true, 30);
        // QuizConfig quizConfig = QuizConfig.createWithoutTimer(false, true);
        Collection<QuestionSet> bundledQuestionSets = QuestionSetManager.loadBundledQuestionSets();
        Iterator<QuestionSet> iterator = bundledQuestionSets.iterator();
        iterator.next();
        QuestionSet questionSet = iterator.next();
        QuizState quizState_ = new QuizState(questionSet, quizConfig);
        final QuizFrame quizFrame = new QuizFrame(quizState_, new ReturnCallback() {
            public void doReturn() {
            // Do nothing
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                quizFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                quizFrame.setVisible(true);
            }
        });
    }

}
