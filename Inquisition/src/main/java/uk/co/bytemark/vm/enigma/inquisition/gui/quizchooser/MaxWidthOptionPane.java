package uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser;

import javax.swing.JOptionPane;

public class MaxWidthOptionPane extends JOptionPane {
    private final int maxWidth;

    public MaxWidthOptionPane(int maxWidth, String message, int type) {
        this.maxWidth = maxWidth;
        setMessage(message);
        setMessageType(type);

    }
        @Override
        public int getMaxCharactersPerLineCount() {
            return maxWidth;
        }
   
}
