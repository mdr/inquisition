package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleChoiceRenderingHelper {

    private final static String[] LETTER_SEQUENCE = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public static int maximumNumberOfOptions() {
        return LETTER_SEQUENCE.length;
    }

    public static String getOptionLabel(int i) {
        return LETTER_SEQUENCE[i];
    }

    public static String optionsTitle(MultipleChoiceQuestion question, boolean stateNumberOfOptionsNeeded) {
        if (question.isSingleOptionMode()) {
            return "Choose one option";
        } else {
            if (stateNumberOfOptionsNeeded) {
                int optionsNeeded = question.numberOfCorrectOptions();
                return "Choose " + optionsNeeded + " option" + (optionsNeeded == 1 ? "" : "s");
            } else {
                return "Choose all that apply";
            }
        }
    }

    public static String getExplanationText(String questionText, String explanationText, List<Option> orderedOptions) {
        Map<String, String> idToLetterMap = makeIdToLetterMap(orderedOptions);
        String substitutedExplanationText = AbstractQuestion.substituteExplanationText(questionText, explanationText);
        String text = QuestionRenderingHelper.syntaxHighlightForJava(substitutedExplanationText);
        return substituteOptionReferences(text, orderedOptions, idToLetterMap);
    }

    public static String getExplanationText(MultipleChoiceQuestionInstance questionInstance) {
        List<Option> orderedOptions = questionInstance.getOrderedOptions();
        MultipleChoiceQuestion question = questionInstance.getQuestion();
        Map<String, String> idToLetterMap = makeIdToLetterMap(orderedOptions);
        String text = question.getSubstitutedExplanationText();
        text = QuestionRenderingHelper.syntaxHighlightForJava(text);
        return substituteOptionReferences(text, orderedOptions, idToLetterMap);
    }

    private static String substituteOptionReferences(String explanationText, List<Option> orderedOptions,
            Map<String, String> idToLetterMap) {
        String resultText = explanationText;
        for (String id : idToLetterMap.keySet())
            resultText = resultText.replaceAll("@" + id + "@", idToLetterMap.get(id));
        resultText = resultText.replaceAll("@allcorrect@", allCorrectString(orderedOptions));
        return resultText;
    }

    private static Map<String, String> makeIdToLetterMap(List<Option> orderedOptions) {
        Map<String, String> idToLetterMap = new HashMap<String, String>();
        int optionCount = 0;
        for (Option option : orderedOptions) {
            idToLetterMap.put(Integer.toString(option.getId()), LETTER_SEQUENCE[optionCount]);
            optionCount++;
        }
        return idToLetterMap;
    }

    /**
     * Returns a string containing all the labels of the correct options joined appropriately by commas and "and"
     */
    private static String allCorrectString(List<Option> orderedOptions) {
        List<String> correctLabels = new ArrayList<String>();
        for (int i = 0; i < orderedOptions.size(); i++) {
            Option option = orderedOptions.get(i);
            if (option.isCorrect()) {
                correctLabels.add(getOptionLabel(i));
            }
        }
        int numberOfCorrectLabels = correctLabels.size();
        if (numberOfCorrectLabels == 0)
            return "";
        else if (numberOfCorrectLabels == 1)
            return correctLabels.get(0);
        else {
            String s = "";
            for (int j = 0; j < numberOfCorrectLabels - 2; j++)
                s += correctLabels.get(j) + ", ";
            s += correctLabels.get(numberOfCorrectLabels - 2) + " and ";
            s += correctLabels.get(numberOfCorrectLabels - 1);
            return s;
        }
    }

}
