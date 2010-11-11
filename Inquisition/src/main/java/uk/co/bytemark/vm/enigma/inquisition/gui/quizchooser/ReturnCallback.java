package uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser;

import java.io.Serializable;

public interface ReturnCallback extends Serializable {
    abstract public void doReturn();

    public class NoOpReturnCallback implements ReturnCallback {
        public void doReturn() {
        // Do nothing
        }
    }
    public final ReturnCallback NO_OP_RETURN_CALLBACK = new NoOpReturnCallback();
}