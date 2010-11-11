package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

public class RichTextField extends javax.swing.JTextPane {

    public RichTextField() {
        InputMap im = getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        im.put(enter, "none");

        // Makes text red
        Style style = addStyle("Function", null);
        StyleConstants.setBackground(style, new Color(230, 230, 255));

        addCaretListener(new CaretListener() {

            public void caretUpdate(CaretEvent e) {
                final StyledDocument document = getStyledDocument();
                String text = getText();
                if (text.length() < 5)
                    return;
                final int fooIndex = text.indexOf("foo");
                if (fooIndex < 0)
                    return;
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        document.setCharacterAttributes(fooIndex, 3, getStyle("Function"), true);

                    }

                });

            }

        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setLayout(new MigLayout(new LC().fill(), new AC().index(0).grow(0).index(1).grow(1)));
                frame.add(new JLabel("Expression:"), new CC().alignX("right").growX(0));
                RichTextField richTextField = new RichTextField();
                richTextField.setDragEnabled(true);
                richTextField.setFont(new Font("Monospaced", Font.PLAIN, 12));
                // frame.add(richTextField, new CC().growX().wrap().height("20!"));
                JScrollPane scrollPane = new JScrollPane(richTextField, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.getViewport().setBackground(Color.WHITE);
                frame.add(scrollPane, new CC().growX().wrap());

                JTextPane dummyTextpane = new JTextPane();
                dummyTextpane.setDragEnabled(true);
                frame.add(new JScrollPane(dummyTextpane), new CC().grow().spanX());

                frame.pack();
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });

    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

}
