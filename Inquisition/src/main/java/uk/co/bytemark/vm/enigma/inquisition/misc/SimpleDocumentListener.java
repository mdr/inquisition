package uk.co.bytemark.vm.enigma.inquisition.misc;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class SimpleDocumentListener implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    public void insertUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    public void removeUpdate(DocumentEvent e) {
        documentChanged(e);
    }

    abstract public void documentChanged(DocumentEvent e);
}
