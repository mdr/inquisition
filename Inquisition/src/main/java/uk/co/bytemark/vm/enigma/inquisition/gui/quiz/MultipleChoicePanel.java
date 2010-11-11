/*
 * MultipleChoicePanel.java
 * 
 * Created on 31 August 2006, 20:48
 */

package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import uk.co.bytemark.vm.enigma.inquisition.questions.Answer;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceAnswer;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestionInstance;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceRenderingHelper;
import uk.co.bytemark.vm.enigma.inquisition.questions.Option;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionRenderingHelper;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSetManager;

public class MultipleChoicePanel extends QuestionPanel {

    private static final Color CORRECT_COLOUR = new Color(0.6f, 0.85f, 0.5f);

    private final MultipleChoiceQuestion question;

    private final boolean stateNumberOfOptionsNeeded;

    private JToggleButton[] toggleButtons;

    private JLabel[] answerLabels;

    private final boolean[] oldCheckBoxEnabledStates;

    private boolean inQuestionMode = true;

    private final MultipleChoiceQuestionInstance questionInstance;

    public MultipleChoicePanel(MultipleChoiceQuestion question, AnswerChangedObserver answerChangedObserver,
            boolean stateNumberOfOptionsNeeded) {
        this.question = question;
        this.stateNumberOfOptionsNeeded = stateNumberOfOptionsNeeded;
        questionInstance = new MultipleChoiceQuestionInstance(question);
        List<Option> options = questionInstance.getOrderedOptions();
        if (options.size() > MultipleChoiceRenderingHelper.maximumNumberOfOptions())
            throw new IllegalArgumentException("Too many options in question: " + question.getOptions().size()
                    + ", max: " + MultipleChoiceRenderingHelper.maximumNumberOfOptions());

        setLayout(new BorderLayout());
        splitPane = makeSplitPane(answerChangedObserver);
        add(splitPane, BorderLayout.CENTER);
        questionTextPane.setCaretPosition(0);
        oldCheckBoxEnabledStates = new boolean[options.size()];

    }

    // Layout hack to attempt to get the options panel to layout correctly
    public void fixDividerLocation() {
        splitPane.resetToPreferredSizes();
    }

    //
    // // Count the height of the labels
    // int total = 100; // Some leeway
    // for (JLabel label : answerLabels) {
    // total += label.getHeight();
    // // optionsPanel.setPreferredSize(new Dimension((int) optionsPanel.getPreferredSize().getWidth(), total));
    // // jSplitPane1.setDividerLocation((int) (this.getHeight() - optionsPanel.getPreferredSize().getHeight()) -
    // // 20);
    // }
    // int location = (this.getHeight() - total);
    // location = Math.max(50, location); // Always allow some of the question text to be visible
    // splitPane.setDividerLocation(location);
    //        
    // }

    @Override
    public MultipleChoiceAnswer getAnswer() {
        List<Option> options = questionInstance.getOrderedOptions();
        Set<Option> optionsSelected = new HashSet<Option>();
        for (int i = 0; i < toggleButtons.length; i++)
            if (toggleButtons[i].isSelected())
                optionsSelected.add(options.get(i));
        return new MultipleChoiceAnswer(optionsSelected);
    }

    @Override
    public void setAnswer(Answer answer) {
        if (!(answer instanceof MultipleChoiceAnswer))
            throw new IllegalArgumentException("Should be of type MultipleChoiceAnswer");
        MultipleChoiceAnswer multipleChoiceAnswer = (MultipleChoiceAnswer) answer;
        if (multipleChoiceAnswer.equals(getAnswer()))
            return;
        Set<Option> optionsSelected = multipleChoiceAnswer.getOptionsSelected();
        List<Option> orderedOptions = questionInstance.getOrderedOptions();
        for (int i = 0; i < toggleButtons.length; i++) {
            toggleButtons[i].setSelected(optionsSelected.contains(orderedOptions.get(i)));
        }

    }

    private JSplitPane makeSplitPane(AnswerChangedObserver answerChangedObserver) {
        JSplitPane splitPane_ = new JSplitPane();
        splitPane_.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane_.setResizeWeight(1.0);

        questionTextPane = new JTextPane();
        questionTextPane.setContentType("text/html");
        questionTextPane.setEditable(false);
        questionTextPane.setText(getQuestionText());
        splitPane_.setTopComponent(new JScrollPane(questionTextPane));

        optionsPanel = createOptionsPanel(answerChangedObserver);
        splitPane_.setBottomComponent(optionsPanel);

        return splitPane_;
    }

    @Override
    public void enterReviewMode() {
        if (!inQuestionMode)
            return;
        inQuestionMode = false;

        List<Option> orderedOptions = questionInstance.getOrderedOptions();
        for (int i = 0; i < toggleButtons.length; i++) {
            oldCheckBoxEnabledStates[i] = toggleButtons[i].isEnabled();
            toggleButtons[i].setEnabled(false);
            if (orderedOptions.get(i).isCorrect()) {
                answerLabels[i].setOpaque(true);
                answerLabels[i].setBackground(CORRECT_COLOUR);
            }
        }
    }

    @Override
    public void enterQuestionMode() {
        if (inQuestionMode)
            return;
        inQuestionMode = true;

        // Add old states if there's been a previous
        for (int i = 0; i < toggleButtons.length; i++) {
            toggleButtons[i].setEnabled(oldCheckBoxEnabledStates[i]);
            answerLabels[i].setOpaque(false);
        }
    }
    @Override
    public String getExplanationText() {
        return MultipleChoiceRenderingHelper.getExplanationText(questionInstance);
    }

    String getQuestionText() {
        return QuestionRenderingHelper.syntaxHighlightForJava(question.getSubstitutedQuestionText());
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    private JSplitPane splitPane;

    private JPanel optionsPanel;

    private JTextPane questionTextPane;

    private JPanel createOptionsPanel(final AnswerChangedObserver answerChangedObserver) {
        List<Option> orderedOptions = questionInstance.getOrderedOptions();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        int numberOfOptions = orderedOptions.size();
        toggleButtons = new JToggleButton[numberOfOptions];
        answerLabels = new JLabel[numberOfOptions];
        for (int i = 0; i < numberOfOptions; i++) {

            String optionLabelText = MultipleChoiceRenderingHelper.getOptionLabel(i) + ")";

            JToggleButton toggleButton = useSingleMode() ? new JRadioButton(optionLabelText) : new JCheckBox(
                    optionLabelText);
            toggleButton.setHorizontalAlignment(SwingConstants.RIGHT);
            toggleButton.setHorizontalTextPosition(SwingConstants.LEFT);
            toggleButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                    answerChangedObserver.answerChanged(getAnswer());

                    if (stateNumberOfOptionsNeeded && !useSingleMode()) {
                        int selectedCount = 0;
                        for (JToggleButton button : toggleButtons) {
                            if (button.isSelected())
                                selectedCount++;
                        }
                        if (selectedCount == question.numberOfCorrectOptions()) {
                            for (JToggleButton button : toggleButtons) {
                                if (!button.isSelected()) {
                                    button.setEnabled(false);
                                } else {
                                    button.setEnabled(true);
                                }
                            }
                        } else {
                            for (JToggleButton button : toggleButtons) {
                                button.setEnabled(true);
                            }
                        }
                    }

                }
            });
            toggleButtons[i] = toggleButton;

            GridBagConstraints toggleButtonConstraints = new GridBagConstraints();
            toggleButtonConstraints.gridx = 0;
            toggleButtonConstraints.gridy = i;
            toggleButtonConstraints.insets = new Insets(0, 0, 0, 10);

            toggleButtonConstraints.anchor = GridBagConstraints.LINE_END;

            panel.add(toggleButton, toggleButtonConstraints);

            JLabel answerLabel = new JLabel();
            answerLabel.setText("<html><body>" + orderedOptions.get(i).getOptionText() + "</body></html>");
            answerLabels[i] = answerLabel;

            GridBagConstraints answerLabelConstraints = new GridBagConstraints();
            answerLabelConstraints.gridx = 1;
            answerLabelConstraints.gridy = i;
            answerLabelConstraints.anchor = GridBagConstraints.LINE_START;
            answerLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
            answerLabelConstraints.weightx = 1.0;

            panel.add(answerLabel, answerLabelConstraints);

        }
        // If these are going to be radio buttons, add them to a button group
        if (useSingleMode()) {
            ButtonGroup group = new ButtonGroup();
            for (JToggleButton toggleButton : toggleButtons)
                group.add(toggleButton);

        }
        panel.setBorder(BorderFactory.createTitledBorder(MultipleChoiceRenderingHelper.optionsTitle(question,
                stateNumberOfOptionsNeeded)));

        return panel;
    }

    private boolean useSingleMode() {
        if (question.isSingleOptionMode())
            return true;
        else if (stateNumberOfOptionsNeeded && question.numberOfCorrectOptions() == 1)
            return true;
        else
            return false;

    }

    public static void main(String[] args) {
        Question question = QuestionSetManager.loadBundledQuestionSets().iterator().next().iterator().next();
        final MultipleChoicePanel multipleChoicePanel = new MultipleChoicePanel((MultipleChoiceQuestion) question,
                new AnswerChangedObserver() {
                    public void answerChanged(Answer answer) {
                    // Do nothing
                    }
                }, false);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.add(multipleChoicePanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }

        });
    }

}
