package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import uk.co.bytemark.vm.enigma.inquisition.gui.screens.editor.AbstractMultipleChoiceQuestionEditorPanel;
import uk.co.bytemark.vm.enigma.inquisition.questions.AbstractQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceRenderingHelper;
import uk.co.bytemark.vm.enigma.inquisition.questions.Option;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionRenderingHelper;

public class MultipleChoiceQuestionEditorPanel extends AbstractMultipleChoiceQuestionEditorPanel implements
        IQuestionEditorPanel {

    private final DefaultTableModel         tableModel;
    private final DefaultListSelectionModel selectionModel;
    private QuestionValidityListener        questionValidityListener;

    public MultipleChoiceQuestionEditorPanel(MultipleChoiceQuestion question) {
        questionTextArea.setText(question.getQuestionText());
        questionTextArea.setCaretPosition(0);
        explanationTextArea.setText(question.getExplanationText());
        explanationTextArea.setCaretPosition(0);
        shufflableCheckBox.setSelected(question.isShufflable());
        singleOptionModeCheckBox.setSelected(question.isSingleOptionMode());
        tableModel = makeOptionsTableModel(question);
        optionsTable.setModel(tableModel);
        optionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        optionsTable.setRowSelectionAllowed(true);
        selectionModel = (DefaultListSelectionModel) optionsTable.getSelectionModel();
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int deleteIndex = selectionModel.getMinSelectionIndex();
                tableModel.removeRow(deleteIndex);
                selectNearestQuestion(deleteIndex);
                updateOkButtonEnabledState();
            }
        });
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.addRow(new Object[] { "option", false });
                updateOkButtonEnabledState();

            }
        });
        moveUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveSelectedItem(-1);
            }

        });
        moveDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveSelectedItem(1);
            }
        });
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int index = selectionModel.getMinSelectionIndex();
                if (index >= 0) {
                    deleteButton.setEnabled(true);
                    moveUpButton.setEnabled(index != 0);
                    moveDownButton.setEnabled(index != optionsTable.getRowCount() - 1);
                } else {
                    deleteButton.setEnabled(false);
                    moveUpButton.setEnabled(false);
                    moveDownButton.setEnabled(false);
                }
            }
        });

        previewQuestionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String rawQuestionText = questionTextArea.getText();
                String questionTextHtml = QuestionRenderingHelper.syntaxHighlightForJava(AbstractQuestion
                        .removeCopyToExplanationBlocks(rawQuestionText));
                launchPreviewDialog(questionTextHtml);
            }
        });
        previewExplanationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String rawQuestionText = questionTextArea.getText();
                String rawExplanationText = explanationTextArea.getText();
                String explanationTextHtml = MultipleChoiceRenderingHelper.getExplanationText(rawQuestionText,
                        rawExplanationText, getOptions());
                launchPreviewDialog(explanationTextHtml);
            }
        });
        singleOptionModeCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOkButtonEnabledState();
            }
        });
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                updateOkButtonEnabledState();
            }
        });
    }

    private void updateOkButtonEnabledState() {
        List<Option> options = getOptions();
        int correctCount = 0;
        for (Option option : options)
            if (option.isCorrect())
                correctCount++;
        boolean singleOptionMode = singleOptionModeCheckBox.isSelected();
        boolean validQuestion = false;
        if (correctCount == 0)
            validQuestion = false;
        else if (correctCount == 1)
            validQuestion = true;
        else if (correctCount > 1)
            validQuestion = !singleOptionMode;
        questionValidityListener.questionValid(validQuestion);
    }

    private void selectNearestQuestion(int index) {
        if (tableModel.getRowCount() > 0) {
            int newIndex = Math.max(0, index - 1);
            selectionModel.setSelectionInterval(newIndex, newIndex);
        }
    }

    private JDialog getParentDialog() { // TODO: Panel shouldn't really know where it's placed
        return (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);
    }

    private void moveSelectedItem(int offset) {
        int index = selectionModel.getMinSelectionIndex();
        tableModel.moveRow(index, index, index + offset);
        selectionModel.setSelectionInterval(index + offset, index + offset);
    }

    private DefaultTableModel makeOptionsTableModel(MultipleChoiceQuestion question) {
        List<Option> options = question.getOptions();
        Object[][] tableRows = new Object[options.size()][];
        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            Object[] row = new Object[] { option.getOptionText(), option.isCorrect() };
            tableRows[i] = row;
        }
        DefaultTableModel model = new DefaultTableModel(tableRows, new String[] { "Option label", "True or false" }) {
            Class<?>[] types = new Class<?>[] { String.class, Boolean.class };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        return model;
    }

    public List<Option> getOptions() {
        MultipleChoiceQuestion.Builder questionBuilder = new MultipleChoiceQuestion.Builder();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String optionText = (String) tableModel.getValueAt(row, 0);
            Boolean outputFromModel = (Boolean) tableModel.getValueAt(row, 1);
            boolean optionCorrect = outputFromModel == null ? false : outputFromModel.booleanValue();
            questionBuilder.option(optionText, optionCorrect);
        }
        return questionBuilder.getOptions();
    }

    public MultipleChoiceQuestion getQuestion() {
        MultipleChoiceQuestion.Builder questionBuilder = new MultipleChoiceQuestion.Builder();
        questionBuilder.options(getOptions());
        questionBuilder.questionText(questionTextArea.getText()).explanationText(explanationTextArea.getText());
        questionBuilder.shufflable(shufflableCheckBox.isSelected()).singleOptionMode(
                singleOptionModeCheckBox.isSelected());
        return questionBuilder.build();
    }

    private void launchPreviewDialog(String html) {
        PreviewHtmlDialog dialog = new PreviewHtmlDialog(getParentDialog(), html);
        dialog.setSize(PreviewHtmlDialog.PREVIEW_HTML_DIALOG_DEFAULT_DIMENSION);
        dialog.setLocationRelativeTo(getParentDialog());
        dialog.setVisible(true);
    }

    public void setQuestionValidityListener(QuestionValidityListener listener) {
        this.questionValidityListener = listener;
        updateOkButtonEnabledState();

    }
}
