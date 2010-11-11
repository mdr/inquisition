package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

public class PreviewHtmlDialog extends JDialog {

    public static final Dimension PREVIEW_HTML_DIALOG_DEFAULT_DIMENSION = new Dimension(640, 480);

    public PreviewHtmlDialog(JFrame parent, String html) {
        super(parent, "Preview", true);
        subconstruct(html);
    }

    private void subconstruct(String html) {
        setLayout(new BorderLayout());
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(html);
        textPane.setCaretPosition(0);
        textPane.setEditable(false);
        add(new JScrollPane(textPane), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new MigLayout(new LC().alignX("right")));
        JButton okButton = new JButton("OK");
        buttonPanel.add(okButton, new CC().sizeGroupX("buttons"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public PreviewHtmlDialog(JDialog parent, String html) {
        super(parent, "Preview", true);
        subconstruct(html);
    }
}
