package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

public class BasicSlotInfo {
    private String fragmentNeeded;
    private String currentText;
    
    BasicSlotInfo(String currentText, String fragmentNeeded) {
        this.currentText = currentText;
        this.fragmentNeeded = fragmentNeeded;
    }
    
    public String getFragmentNeeded() {
        return fragmentNeeded;
    }
    
    public String getCurrentText() {
        return currentText;
    }
    
}