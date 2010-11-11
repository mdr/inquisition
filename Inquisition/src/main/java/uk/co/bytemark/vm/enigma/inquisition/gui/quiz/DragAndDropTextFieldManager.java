/**
 * 
 */
package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextField;

import uk.co.bytemark.vm.enigma.inquisition.questions.Answer;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropAnswer;

public class DragAndDropTextFieldManager {
    private Map<Integer, Set<JTextField>> textFieldsMap = new HashMap<Integer, Set<JTextField>>();

    private Map<Integer, String> correctTextMap = new HashMap<Integer, String>();

    public void addTextField(Integer id, String correctText, JTextField textField) {
        if (!textFieldsMap.containsKey(id))
            textFieldsMap.put(id, new HashSet<JTextField>());
        Set<JTextField> textFields = textFieldsMap.get(id);
        textFields.add(textField);
        if (correctTextMap.containsKey(id)) {
            String previousCorrectText = correctTextMap.get(id);
            if (!correctText.equals("") && !previousCorrectText.equals(correctText))
                throw new AssertionError("Inconsistent generation of text fields in drag and drop panel. id=" + id
                        + ", previousCorrectText=" + previousCorrectText + ", correctText=" + correctText);
        } else {
            correctTextMap.put(id, correctText);
        }

    }

    public void colourTextFieldsAccordingToWhetherTheyAreEmptyOrNot() {
        for (Integer id : textFieldsMap.keySet()) {
            Set<JTextField> textFields = textFieldsMap.get(id);
            for (JTextField textField : textFields) {
                if (textField.getText().equals(""))
                    textField.setBackground(DragAndDropPanel.NORMAL_COLOUR);
                else
                    textField.setBackground(DragAndDropFragmentBinPanel.FRAGMENT_FILL_COLOUR);
            }
        }

    }

    public void colourTextFieldsAccordingToCorrectness() {
        for (Integer id : textFieldsMap.keySet()) {
            Set<JTextField> textFields = textFieldsMap.get(id);
            String correctText = correctTextMap.get(id);
            for (JTextField textField : textFields) {
                if (textField.getText().equals(correctText))
                    textField.setBackground(DragAndDropPanel.CORRECT_COLOUR);
                else
                    textField.setBackground(DragAndDropPanel.INCORRECT_COLOUR);
            }
        }
    }

    public int getLargestId() {
        int largest = -1;
        for (Integer id : textFieldsMap.keySet())
            if (id > largest)
                largest = id;
        return largest;
    }

    public void checkIdsAreContiguous() {
        int largestId = getLargestId();
        for (int id = 0; id <= largestId; id++) {
            if (!textFieldsMap.containsKey(id)) {
                throw new RuntimeException("Slot ids are not continuous");
            }
        }
    }

    public Answer getAnswer() {
        checkIdsAreContiguous();

        List<String> slotAnswers = new ArrayList<String>();
        for (int id = 0; id <= getLargestId(); id++) {
            Set<JTextField> textFields = textFieldsMap.get(id);
            String currentText = null;
            for (JTextField textField : textFields) {
                String actualText = textField.getText();
                if (!actualText.equals("")) {
                    currentText = actualText;
                    break;
                }
            }
            if (currentText == null)
                currentText = "";
            slotAnswers.add(currentText);

        }
        return new DragAndDropAnswer(slotAnswers);
    }

    public void blankAllTextFields() {
        for (Integer id : textFieldsMap.keySet())
            for (JTextField textField : textFieldsMap.get(id))
                textField.setText("");

    }

    public void setFields(List<String> slotAnswers) {
        int numberOfSlotAnswers = slotAnswers.size();
        if (numberOfSlotAnswers > getLargestId() + 1)
            throw new AssertionError("Too many slot answers: " + slotAnswers + "; ids = " + textFieldsMap.keySet());

        for (int id = 0; id < numberOfSlotAnswers; id++) {
            String slotText = slotAnswers.get(id);
            Set<JTextField> textFields = textFieldsMap.get(id);
            for (JTextField textField : textFields) {

                textField.setText(slotText);
            }

        }

    }
}