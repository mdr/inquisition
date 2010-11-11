package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import uk.co.bytemark.vm.enigma.inquisition.gui.screens.editor.AbstractQuestionEditorMainPanel;
import uk.co.bytemark.vm.enigma.inquisition.misc.SimpleDocumentListener;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionType;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet.QuestionSetBuilder;

public class QuestionEditorMainPanel extends AbstractQuestionEditorMainPanel {

    private static final int       DEFAULT_RECOMMENDED_TIME          = 120;

    private static final String    DEFAULT_DESCRIPTION;

    static {
        DEFAULT_DESCRIPTION = "Questions on ...\n" + "<hr>\n" + "<b>Info:</b><br>\n" + "<ul>\n"
                + "<li>Maintainer: Joe Bloggs <tt>&lt;joebloggs@somewhere.com&gt;</tt>\n"
                + "<li>Home page: <a href=\"http://website.com\">http://website.com</a>\n" + "<li>Version: 2\n"
                + "<li>Date published: 13/October/2008\n" + "<li>Licence: (Public domain, Creative Commons etc)\n"
                + "</ul>\n";
    }

    private static final String    DEFAULT_MULTIPLE_CHOICE_QUESTION_TEXT;

    static {
        DEFAULT_MULTIPLE_CHOICE_QUESTION_TEXT = "This can include Java code with automatic syntax highlighting, e.g.\n<java>\n"
                + "  // Java code...\n  public static void main(String[] args) { ... }\n</java>";
    }

    private static final String    DEFAULT_MULTIPLE_CHOICE_EXPLANATION_TEXT;

    static {
        DEFAULT_MULTIPLE_CHOICE_EXPLANATION_TEXT = "The explanation can reference the options directly, "
                + "e.g., @1@, @2@ and @3@, or all the correct options, with @allcorrect@.";
    }

    private static final String    DEFAULT_DRAG_AND_DROP_CHOICE_QUESTION_TEXT;

    static {
        DEFAULT_DRAG_AND_DROP_CHOICE_QUESTION_TEXT = //
        "Complete the following Java class:\n" + //
                "<CopyToExplanation>\n" + //
                "<java>\n" + //
                "public class <slot>Foobar</slot> {\n" + //
                "   private final <slot>int</slot> field;\n" + //
                "}" + //
                "</java>\n" + //
                "</CopyToExplanation>\n";

    }

    private static final String    DEFAULT_DRAG_AND_DROP_CHOICE_EXPLANATION_TEXT;

    static {
        DEFAULT_DRAG_AND_DROP_CHOICE_EXPLANATION_TEXT = //
        "The correct answer is:\n" + //
                "<CopyFromQuestion/>\n" + //
                "(The appropriate section gets copied from the question, but with the slots filled in correctly)";
    }

    private static final String    DEFAULT_NAME                      = "Name";
    private static final String    DEFAULT_CATEGORY                  = "Category:Subcategory";

    private boolean                suppressDocumentDirtyingListeners = false;
    private DirtyingActionListener dirtyingActionListener            = new DirtyingActionListener() {
                                                                         public void dirtyingActionHappened() {
                                                                         // Do nothing
                                                                         }
                                                                     };

    public QuestionEditorMainPanel() {
        setUpNewQuestionSet();
        moveUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveQuestionListItem(questionJList.getSelectedIndex(), questionJList.getSelectedIndex() - 1);
            }
        });
        moveDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveQuestionListItem(questionJList.getSelectedIndex(), questionJList.getSelectedIndex() + 1);
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedQuestion();
            }
        });
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedQuestion(false);
            }
        });
        questionJList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                refreshQuestionButtonEnabledStates();
            }
        });
        addNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNewQuestion();
            }
        });
        previewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previewDescriptionHtml();
            }

        });
        questionJList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    if (!questionJList.isSelectionEmpty()) {
                        editSelectedQuestion(false);
                    }
                }
            }
        });
        recommendedTimeTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                int result;
                try {
                    result = Integer.parseInt(recommendedTimeTextField.getText());
                } catch (NumberFormatException e) {
                    result = -1;
                }
                if (result <= 0)
                    recommendedTimeTextField.setText(Integer.toString(DEFAULT_RECOMMENDED_TIME));
            }
        });
        nameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (isWhitespace(nameTextField))
                    nameTextField.setText(DEFAULT_NAME);
            }
        });
        categoryTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (isWhitespace(categoryTextField))
                    categoryTextField.setText(DEFAULT_CATEGORY);
            }
        });
        addDirtyingListenerToDocument(nameTextField.getDocument());
        addDirtyingListenerToDocument(recommendedTimeTextField.getDocument());
        addDirtyingListenerToDocument(descriptionTextPane.getDocument());
        addDirtyingListenerToDocument(categoryTextField.getDocument());
        refreshQuestionButtonEnabledStates();

    }

    private boolean isWhitespace(JTextField textField) {
        String text = textField.getText();
        return text.matches("\\s*");
    }

    private void previewDescriptionHtml() {
        JFrame frame = getParentFrame();
        PreviewHtmlDialog dialog = new PreviewHtmlDialog(frame, descriptionTextPane.getText());
        dialog.setSize(PreviewHtmlDialog.PREVIEW_HTML_DIALOG_DEFAULT_DIMENSION);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void addNewQuestion() {
        JFrame frame = getParentFrame();
        ChooseQuestionTypeDialog dialog = new ChooseQuestionTypeDialog(frame);
        dialog.setSize(300, 240);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        if (dialog.okSelected()) {
            QuestionType questionType = dialog.getQuestionType();
            Question question;
            if (questionType == QuestionType.MULTIPLE_CHOICE) {
                question = new MultipleChoiceQuestion.Builder().questionText(DEFAULT_MULTIPLE_CHOICE_QUESTION_TEXT)
                        .explanationText(DEFAULT_MULTIPLE_CHOICE_EXPLANATION_TEXT).option("Option 1", true).option(
                                "Option 2", false).option("Option 3", false).build();
            } else { // DRAG AND DROP
                question = new DragAndDropQuestion(DEFAULT_DRAG_AND_DROP_CHOICE_QUESTION_TEXT,
                        DEFAULT_DRAG_AND_DROP_CHOICE_EXPLANATION_TEXT, Arrays.asList("Extra fragment"), false);
            }
            DefaultListModel model = (DefaultListModel) questionJList.getModel();
            int insertIndex = questionJList.getSelectedIndex() + 1;
            model.add(insertIndex, question);
            questionJList.setSelectedIndex(insertIndex);
            editSelectedQuestion(true);
        }
    }

    private JFrame getParentFrame() {
        return (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
    }

    private void editSelectedQuestion(boolean newQuestion) {
        final int index = questionJList.getSelectedIndex();
        Question question = getQuestion(index);
        JFrame frame = getParentFrame();
        JPanel questionEditorPanel;
        String title;
        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
            questionEditorPanel = new MultipleChoiceQuestionEditorPanel(multipleChoiceQuestion);
            title = "Multiple Choice Question";
        } else if (question instanceof DragAndDropQuestion) {
            DragAndDropQuestion dragAndDropQuestion = (DragAndDropQuestion) question;
            questionEditorPanel = new DragAndDropQuestionEditorPanel(dragAndDropQuestion);
            title = "Drag and Drop Question";
        } else {
            throw new AssertionError("Unexpected type of question");
        }
        QuestionDialog questionDialog = new QuestionDialog(frame, title, questionEditorPanel);
        questionDialog.pack();
        questionDialog.setLocationRelativeTo(frame);
        questionDialog.setVisible(true);
        if (questionDialog.okSelected()) {
            Question replacementQuestion = questionDialog.getQuestion();
            if (replacementQuestion.equals(question)) {
                if (newQuestion)
                    dirtyingActionListener.dirtyingActionHappened();
            } else {
                replaceQuestion(index, replacementQuestion);
                questionJList.setSelectedIndex(index);
                dirtyingActionListener.dirtyingActionHappened();
            }
        } else { // Cancelled
            if (newQuestion) {
                DefaultListModel model = (DefaultListModel) questionJList.getModel();
                model.remove(index);
                selectNearestQuestion(index);
            }
        }

    }

    private void replaceQuestion(final int index, Question replacementQuestion) {
        DefaultListModel model = (DefaultListModel) questionJList.getModel();
        model.remove(index);
        model.add(index, replacementQuestion);
    }

    private Question getQuestion(int index) {
        return (Question) questionJList.getModel().getElementAt(index);
    }

    private void addDirtyingListenerToDocument(Document document) {
        document.addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                if (!suppressDocumentDirtyingListeners)
                    dirtyingActionListener.dirtyingActionHappened();
            }
        });
    }

    private void deleteSelectedQuestion() {
        int deleteIndex = questionJList.getSelectedIndex();
        DefaultListModel model = (DefaultListModel) questionJList.getModel();
        model.remove(deleteIndex);
        dirtyingActionListener.dirtyingActionHappened();
        selectNearestQuestion(deleteIndex);
    }

    private void selectNearestQuestion(int index) {
        DefaultListModel model = (DefaultListModel) questionJList.getModel();
        if (!model.isEmpty())
            questionJList.setSelectedIndex(Math.max(0, index - 1));
    }

    private void refreshQuestionButtonEnabledStates() {
        int index = questionJList.getSelectedIndex();
        boolean itemSelected = index >= 0;
        if (itemSelected) {
            deleteButton.setEnabled(true);
            editButton.setEnabled(true);
            boolean firstItemSelected = index == 0;
            moveUpButton.setEnabled(!firstItemSelected);
            boolean lastItemSelected = index == questionJList.getModel().getSize() - 1;
            moveDownButton.setEnabled(!lastItemSelected);
        } else {
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
        }
    }

    private void moveQuestionListItem(int currentPosition, int newPosition) {
        DefaultListModel model = (DefaultListModel) questionJList.getModel();
        Object item = model.remove(currentPosition);
        model.add(newPosition, item);
        questionJList.setSelectedIndex(newPosition);
        dirtyingActionListener.dirtyingActionHappened();
    }

    public void setUpNewQuestionSet() {
        suppressDocumentDirtyingListeners = true;
        nameTextField.setText("");
        categoryTextField.setText("");
        recommendedTimeTextField.setText(Integer.toString(DEFAULT_RECOMMENDED_TIME));
        nameTextField.setText(DEFAULT_NAME);
        categoryTextField.setText(DEFAULT_CATEGORY);
        descriptionTextPane.setText(DEFAULT_DESCRIPTION);
        descriptionTextPane.setCaretPosition(0);
        questionJList.setModel(new DefaultListModel());
        suppressDocumentDirtyingListeners = false;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        suppressDocumentDirtyingListeners = true;

        nameTextField.setText(questionSet.getName());
        categoryTextField.setText(questionSet.getCategorySequence());
        recommendedTimeTextField.setText(Integer.toString(questionSet.getRecommendedTimePerQuestion()));
        descriptionTextPane.setText(questionSet.getDescription());
        descriptionTextPane.setCaretPosition(0);

        DefaultListModel questionsListModel = new DefaultListModel();
        for (Question question : questionSet.getQuestions()) {
            questionsListModel.addElement(question);
        }
        questionJList.setModel(questionsListModel);
        suppressDocumentDirtyingListeners = false;
    }

    public QuestionSet getQuestionSet() {
        QuestionSetBuilder questionSetBuilder = new QuestionSet.QuestionSetBuilder();
        int numberOfQuestions = getNumberOfQuestions();
        for (int i = 0; i < numberOfQuestions; i++)
            questionSetBuilder.addQuestion(getQuestion(i));

        int recommendedTimePerQuestion;
        try {
            recommendedTimePerQuestion = Integer.parseInt(recommendedTimeTextField.getText());
        } catch (NumberFormatException e) {
            recommendedTimePerQuestion = -1;
        }
        if (recommendedTimePerQuestion < 0)
            recommendedTimePerQuestion = DEFAULT_RECOMMENDED_TIME;

        String name = nameTextField.getText();
        String description = descriptionTextPane.getText();
        String category = categoryTextField.getText();
        return questionSetBuilder.name(name).category(category).description(description).recommendedTime(
                recommendedTimePerQuestion).build();
    }

    private int getNumberOfQuestions() {
        int numberOfQuestions = questionJList.getModel().getSize();
        return numberOfQuestions;
    }

    public void setDirtyingActionListener(DirtyingActionListener dirtyingActionListener) {
        this.dirtyingActionListener = dirtyingActionListener;
    }

}
