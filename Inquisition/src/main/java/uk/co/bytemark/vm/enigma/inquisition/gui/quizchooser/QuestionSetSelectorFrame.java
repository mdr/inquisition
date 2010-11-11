/*
 * TestChooserFrame.java
 * 
 * Created on 03 September 2006, 21:21
 */
package uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import uk.co.bytemark.vm.enigma.inquisition.gui.editor.QuestionEditor;
import uk.co.bytemark.vm.enigma.inquisition.gui.images.Icons;
import uk.co.bytemark.vm.enigma.inquisition.gui.misc.LookAndFeelMenu;
import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
import uk.co.bytemark.vm.enigma.inquisition.gui.quiz.QuizFrame;
import uk.co.bytemark.vm.enigma.inquisition.gui.quiz.ResizeListener;
import uk.co.bytemark.vm.enigma.inquisition.gui.screens.AboutDialog;
import uk.co.bytemark.vm.enigma.inquisition.gui.screens.AbstractQuestionSetSelectorFrame;
import uk.co.bytemark.vm.enigma.inquisition.misc.Constants;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSetManager;
import uk.co.bytemark.vm.enigma.inquisition.quiz.QuizState;

public class QuestionSetSelectorFrame extends AbstractQuestionSetSelectorFrame {

    private static final Logger  LOGGER   = Logger.getLogger(QuestionSetSelectorFrame.class.getName());

    private final ReturnCallback callback = new ReturnCallback() {
                                              public void doReturn() {
                                                  setVisible(true);
                                                  beginQuizButton.setEnabled(true);
                                              }
                                          };

    public QuestionSetSelectorFrame(Collection<QuestionSet> initialQuestionSets) {
        createComponents();

        if (Icons.FAVICON.isAvailable())
            setIconImage(Icons.FAVICON.getImage());
        relayoutContinuallyOnWindowResize();
        addComponentListener(new ResizeListener());
        timeSlider.setValue(0);
        disableEditor();
        setEnabledStateOfQuestionSetInfoComponents(false);

        initialiseQuestionSetTree(initialQuestionSets);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initialiseQuestionSetTree(Collection<QuestionSet> initialQuestionSets) {
        final QuestionSetTree tree = getQuestionSetTree();
        tree.initialise(initialQuestionSets);
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean doubleClicked = e.getClickCount() == 2;
                if (doubleClicked && tree.isAQuestionSetSelected()) {
                    beginQuiz();
                }
            }
        };
        tree.addMouseListener(ml);
    }

    private void disableEditor() {
        // toolsMenu.setVisible(false);
        // editorMenuItem.setVisible(false);
        editQuizButton.setVisible(false); // TODO: Remove this
    }

    private void relayoutContinuallyOnWindowResize() {
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
    }

    @Override
    protected JTree createQuestionSetTree() {
        return new QuestionSetTree();
    }

    @Override
    protected void addQuizFromWebMenuItemActionPerformed() {
        addQuizFromWebButtonActionPerformed();
    }

    @Override
    protected void aboutMenuItemActionPerformed() {
        JDialog aboutDialog = new AboutDialog(this, Constants.getAboutText());
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }

    @Override
    protected void editTestButtonClicked() {
        editorMenuItemSelected();
    }

    @Override
    protected void editorMenuItemSelected() {
        // QuestionSet set = getQuestionSetTree().getSelectedQuestionSet();
        // setVisible(false);
        // if (set != null)
        // new TestEditor(set, callback).setVisible(true);
        // else
        // new TestEditor(callback).setVisible(true);
        QuestionEditor questionEditor = new QuestionEditor();
        questionEditor.pack();
        questionEditor.setLocationRelativeTo(this);
        questionEditor.setVisible(true);
    }

    private QuestionSetTree getQuestionSetTree() {
        return (QuestionSetTree) questionSetTree;
    }

    @Override
    protected void addTestFromFileMenuItemActionPerformed() {
        addQuizFromFileButtonActionPerformed();
    }

    @Override
    protected void helpMenuActionPerformed() {
    // TODO add your handling code here:
    }

    @Override
    protected void addQuizFromWebButtonActionPerformed() {
        WebPageFileChooserDialog dialog = new WebPageFileChooserDialog(this, Constants.REPOSITORY_URL);
        dialog.setVisible(true);
    }

    @Override
    protected void addQuizFromFileButtonActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Questions File");
        SuffixFileFilter.setSoleFileFilter(fileChooser, Constants.QUESTION_SET_FILE_FILTER);
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        String fileName = fileChooser.getSelectedFile().getAbsolutePath();
        LOGGER.info("Importing question set from file " + fileName);
        InputStream stream = null;
        try {
            QuestionSet questionSet;
            stream = new FileInputStream(fileName);
            questionSet = QuestionSetManager.getQuestionSetFromInputStream(stream);
            addQuestionSet(questionSet);
        } catch (ParseException e) {
            LOGGER.log(Level.INFO, "Could not parse file " + fileName, e);
            JOptionPane.showMessageDialog(this, "Could not parse " + fileName, "Could not parse file",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Could not open file " + fileName, e);
            JOptionPane.showMessageDialog(this, "Could not open " + fileName, "Could not open file",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not close stream", e);
            }
        }
    }

    private void addQuestionSet(QuestionSet questionSet) {
        TreePath path = getQuestionSetTree().addQuestionSetToTree(questionSet);
        getQuestionSetTree().setSelectionPath(path);
    }

    @Override
    protected void beginQuizButtonActionPerformed() {
        beginQuiz();
    }

    void beginQuiz() {
        beginQuizButton.setEnabled(false); // Make sure it can't be clicked again before we're done
        // TODO: (Why do we need this?)
        QuizConfig quizConfig = getQuizConfig();
        QuestionSet questionSet = getQuestionSetTree().getSelectedQuestionSet();
        QuizState quizState = new QuizState(questionSet, quizConfig);
        beginQuiz(quizState);
    }

    private void beginQuiz(QuizState quizState) {
        QuizFrame quizFrame = new QuizFrame(quizState, callback);
        quizFrame.setLocationRelativeTo(this);
        this.setVisible(false);
        quizFrame.setVisible(true);
    }

    private QuizConfig getQuizConfig() {
        boolean shuffleQuestionOrder = shuffleCheckBox.isSelected();
        boolean stateNumberOfOptionsNeeded = stateOptionsNumberCheckBox.isSelected();
        boolean useTimer = useTimerCheckBox.isSelected();
        int time = 60 * getTimeSliderTime();
        if (useTimer)
            return QuizConfig.createWithTimer(shuffleQuestionOrder, stateNumberOfOptionsNeeded, time);
        else
            return QuizConfig.createWithoutTimer(shuffleQuestionOrder, stateNumberOfOptionsNeeded);
    }

    @Override
    protected void exitMenuItemActionPerformed() {
        LOGGER.info("Closing QuestionSetSelectorFrame");
        dispose();
    }

    void addWebExam(String urlString) {
        LOGGER.info("Importing exam from web: " + urlString);
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Invalid URL when opening web exam: " + urlString, e);
            return;
        }
        try {
            QuestionSet questionSet = QuestionSetManager.getQuestionSetFromInputStream(url.openStream());
            addQuestionSet(questionSet);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Could not parse file from URL: " + url, "Could not open file",
                    JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.WARNING, "Could not parse file at " + url, e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not open file from URL: " + url, "Could not open file",
                    JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.WARNING, "Could not open file at " + url, e);
        }
    }

    @Override
    protected JMenu createLAFMenu() {
        return new LookAndFeelMenu(this);
    }

    @Override
    protected void questionSetTreeValueChanged() {
        if (getQuestionSetTree().isAQuestionSetSelected()) {
            setEnabledStateOfQuestionSetInfoComponents(true);
            QuestionSet questionSet = getQuestionSetTree().getSelectedQuestionSet();
            int numberOfDragAndDrop = questionSet.numberOfType(DragAndDropQuestion.class);
            int numberOfMultipleChoice = questionSet.numberOfType(MultipleChoiceQuestion.class);
            int numberOfQuestions = questionSet.numberOfQuestions();
            String numberOfQuestionsInfo = numberOfQuestions + " (" + numberOfMultipleChoice + " multiple choice, "
                    + numberOfDragAndDrop + " drag and drop)";
            numberOfQuestionsText.setText(numberOfQuestionsInfo);
            descriptionText.setText(questionSet.getDescription());
            descriptionText.setCaretPosition(0);
            int recommendedTime = questionSet.getRecommendedTimeForAllQuestions();
            setTimeSliderValue(recommendedTime);
        } else {
            setEnabledStateOfQuestionSetInfoComponents(false);
            numberOfQuestionsText.setText("");
            descriptionText.setText("");
        }
    }

    private void setEnabledStateOfQuestionSetInfoComponents(boolean enabled) {
        numberOfQuestionsText.setEnabled(enabled);
        descriptionText.setEnabled(enabled);
        beginQuizButton.setEnabled(enabled);
        editQuizButton.setEnabled(enabled);
        jLabel1.setEnabled(enabled); // TODO: Get these renamed
        jLabel3.setEnabled(enabled);

        exportQuestionSetAsHtmlMenuItem.setEnabled(enabled);

        // Options panel
        timeSlider.setEnabled(enabled);
        timeTextLabel.setEnabled(enabled);
        shuffleCheckBox.setEnabled(enabled);
        stateOptionsNumberCheckBox.setEnabled(enabled);
        useTimerCheckBox.setEnabled(enabled);
    }

    @Override
    protected void useTimerCheckBoxActionPerformed() {
        boolean useTimer = useTimerCheckBox.isSelected();
        timeSlider.setEnabled(useTimer);
        timeTextLabel.setEnabled(useTimer);
    }

    // TODO: Refactor into a TimeSlider JComponent
    @Override
    protected void timeSliderStateChanged() {
        int totalMinutes = getTimeSliderTime();
        String timeText = getTimeText(totalMinutes);

        timeTextLabel.setText(timeText);
    }

    private String getTimeText(int totalMinutes) {
        int hours = totalMinutes / 60;
        int extraMinutes = totalMinutes - hours * 60;
        String minuteS = (extraMinutes == 1) ? "" : "s";
        String hourS = (hours == 1) ? "" : "s";
        String timeText;
        if (hours == 0)
            timeText = String.format("%d minute" + minuteS, extraMinutes);
        else if (extraMinutes == 0)
            timeText = String.format("%d hour" + hourS, hours);
        else
            timeText = String.format("%d hour" + hourS + " and %d minute" + minuteS, hours, extraMinutes);
        return timeText;
    }

    private void setTimeSliderValue(int recommendedTime) {
        int sliderPosition = (recommendedTime / 60) / 2 - 1;
        timeSlider.setValue(sliderPosition);
    }

    private int getTimeSliderTime() {
        return timeSlider.getValue() * 2 + 2;
    }

    public static void main(String[] args) {
        QuestionSet mockQuestionSet = new QuestionSet("Mock Question Set", "Mock", 3, "mock", Collections
                .<Question> emptyList());
        final QuestionSetSelectorFrame quizSelectorFrame = new QuestionSetSelectorFrame(Collections
                .singletonList(mockQuestionSet));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                quizSelectorFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                quizSelectorFrame.setVisible(true);
            }
        });
    }

    @Override
    protected void resumeQuizMenuItemActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        SuffixFileFilter.setSoleFileFilter(fileChooser, Constants.QUIZ_SUFFIX_FILE_FILTER);
        fileChooser.setDialogTitle("Resume Quiz");
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        String quizStateFileName = fileChooser.getSelectedFile().getAbsolutePath();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(quizStateFileName);
            ObjectInputStream ois = new ObjectInputStream(fileInputStream);
            QuizState quizState = (QuizState) ois.readObject();
            beginQuiz(quizState);
        } catch (InvalidClassException e) {
            LOGGER.log(Level.FINER, "Could not resume quiz at " + quizStateFileName, e);
            String message = "Could not resume quiz '" + quizStateFileName
                    + "': the quiz was saved with a different version of Inquisition.";
            showCouldNotResumeQuizDialog(message);
        } catch (Exception e) {
            LOGGER.log(Level.FINER, "Could not resume quiz at " + quizStateFileName, e);
            String message = "Could not resume quiz '" + quizStateFileName + "' due to error '" + e.getMessage() + "'";
            showCouldNotResumeQuizDialog(message);
        }

    }

    private void showCouldNotResumeQuizDialog(String message) {
        JOptionPane pane = new MaxWidthOptionPane(100, message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Could Not Resume Quiz");
        dialog.setVisible(true);
    }

    @Override
    protected void exportQuestionSetAsHtml() {
        QuestionSet questionSet = getQuestionSetTree().getSelectedQuestionSet();
        JDialog dialog = new RenderQuestionAsHtmlDialog(this, questionSet);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

}
