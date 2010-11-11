package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import uk.co.bytemark.vm.enigma.inquisition.gui.editor.IQuestionEditorPanel.QuestionValidityListener;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;

public class QuestionDialog extends JDialog implements QuestionValidityListener {

    private boolean                    ok = false;
    private final IQuestionEditorPanel questionEditorPanel;
    private final JButton              okButton;

    public QuestionDialog(JFrame parent, String title, JPanel questionEditorPanel) {
        super(parent, title, true);
        if (!(questionEditorPanel instanceof IQuestionEditorPanel))
            throw new IllegalArgumentException("questionEditorpanel must be an IQuestionEditorPanel");
        this.questionEditorPanel = (IQuestionEditorPanel) questionEditorPanel;
        setLayout(new BorderLayout());
        add(questionEditorPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new MigLayout(new LC().alignX("right")));
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton, new CC().sizeGroupX("buttons"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        okButton = new JButton("OK");
        buttonPanel.add(okButton, new CC().sizeGroupX("buttons"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok = true;
                dispose();
            }
        });
        add(buttonPanel, BorderLayout.SOUTH);
        this.questionEditorPanel.setQuestionValidityListener(this);
    }

    public boolean okSelected() {
        return ok;
    }

    public Question getQuestion() {
        return questionEditorPanel.getQuestion();
    }

    public void questionValid(boolean valid) {
        okButton.setEnabled(valid);
    }

}
