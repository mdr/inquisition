package uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
import uk.co.bytemark.vm.enigma.inquisition.gui.screens.AbstractRenderAsHtmlDialog;
import uk.co.bytemark.vm.enigma.inquisition.misc.Constants;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.quiz.HTMLQuizRenderer;

public class RenderQuestionAsHtmlDialog extends AbstractRenderAsHtmlDialog {

    private final QuestionSet questionSet;

    public RenderQuestionAsHtmlDialog(Frame parent, QuestionSet questionSet) {
        super(parent, true);
        this.questionSet = questionSet;
        getOkButton().setEnabled(false);
        setUpListeners();
    }

    private void setUpListeners() {
        getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean succeeded = writeHTML();
                if (succeeded) {
                    dispose();
                }
            }
        });
        getChooseOutputFileButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseOutputFile();
            }

        });
        getShowAnswersCheckBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBox addLinksCheckBox = getAddLinksCheckBox();
                boolean showAnswers = getShowAnswersCheckBox().isSelected();
                addLinksCheckBox.setEnabled(showAnswers);
                if (!showAnswers)
                    addLinksCheckBox.setSelected(false);
            }

        });
        getOutputFileTextField().getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                outputTextFieldChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                outputTextFieldChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                outputTextFieldChanged();
            }

        });
    }

    private void outputTextFieldChanged() {
        JTextField outputFileTextField = getOutputFileTextField();
        boolean hasSomeText = !outputFileTextField.getText().equals("");
        getOkButton().setEnabled(hasSomeText);
    }

    private boolean writeHTML() {
        JTextField outputFileTextField = getOutputFileTextField();
        boolean showAnswers = getShowAnswersCheckBox().isSelected();
        boolean addLinks = getAddLinksCheckBox().isSelected();
        File outputFile = new File(outputFileTextField.getText());
        if (outputFile.exists()) {
            int confirmStatus = JOptionPane.showConfirmDialog(this, "'" + outputFile.getAbsolutePath()
                    + "' already exists. Do you want to overwrite it?", "Confirm Overwrite",
                    JOptionPane.OK_CANCEL_OPTION);
            if (confirmStatus != JOptionPane.OK_OPTION) {
                return false;
            }
        }
        QuizConfig quizConfig = QuizConfig.createWithTimer(false, true, 30);
        HTMLQuizRenderer quizRenderer = new HTMLQuizRenderer( quizConfig, showAnswers, false, addLinks);
        String html = quizRenderer.asHtml(questionSet);

        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(outputFile);
            printWriter.write(html);
            printWriter.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Could not export HTML to file '" + outputFile + "' due to error '"
                    + e.getMessage() + "'", "Could not export HTML", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        JOptionPane.showMessageDialog(this, "Successfully exported questions as HTML to '" + outputFile,
                " Exported Question Set as HTML ", JOptionPane.PLAIN_MESSAGE);
        return true;
    }

    private void chooseOutputFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Question Set as HTML");
        SuffixFileFilter.setSoleFileFilter(fileChooser, Constants.HTML_FILE_FILTER);
        int resultStatus = fileChooser.showSaveDialog(this);
        if (resultStatus == JFileChooser.APPROVE_OPTION) {
            String htmlFileName = fileChooser.getSelectedFile().getAbsolutePath();
            File htmlFile = new File(htmlFileName);
            if (!Constants.HTML_FILE_FILTER.accept(htmlFile)) {
                htmlFileName = Constants.HTML_FILE_FILTER.addSuffixTo(htmlFileName);
            }
            getOutputFileTextField().setText(htmlFileName);
        }

    }

}
