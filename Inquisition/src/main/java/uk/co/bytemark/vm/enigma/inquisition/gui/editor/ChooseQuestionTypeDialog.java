package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionType;

public class ChooseQuestionTypeDialog extends JDialog {

    private boolean       ok = false;
    private final JList   selectionList;
    private final JButton okButton;

    public ChooseQuestionTypeDialog(JFrame parent) {
        super(parent, "Choose a Question Type", true);
        setLayout(new BorderLayout());
        // JPanel choicePanel = new JPanel(new MigLayout(new LC().fill()));
        selectionList = new JList();
        selectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListModel model = new AbstractListModel() {
            public Object getElementAt(int index) {
                return QuestionType.values()[index].getName();
            }

            public int getSize() {
                return QuestionType.values().length;
            }

        };
        selectionList.setModel(model);
        selectionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    if (!selectionList.isSelectionEmpty()) {
                        ok = true;
                        dispose();
                    }
                }
            }
        });

        // choicePanel.add(selectionList);
        add(new JScrollPane(selectionList), BorderLayout.CENTER);
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
        selectionList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                setOkButtonEnabledState();
            }

        });
        setOkButtonEnabledState();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setOkButtonEnabledState() {
        okButton.setEnabled(selectionList.getSelectedIndex() >= 0);
    }

    public boolean okSelected() {
        return ok;
    }

    public QuestionType getQuestionType() {
        return QuestionType.values()[selectionList.getSelectedIndex()];
    }

}
