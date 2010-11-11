package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DragAndDropRenderingHelper {

    private static final Pattern SLOT_REGEX = Pattern.compile("<slot>(.*?)</slot>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public static String optionsTitle(DragAndDropQuestion question) {
        if (question.canReuseFragments()) {
            return "Use the following fragments zero or many times";
        } else {
            return "Use each fragment at most once";
        }
    }

    public static String getQuestionText(DragAndDropQuestion question, boolean includeSlotValues) {
        return QuestionRenderingHelper.syntaxHighlightForJava(DragAndDropRenderingHelper.substituteSlots(question, question.getSubstitutedQuestionText(), includeSlotValues));
    }

    public static String getExplanationText(DragAndDropQuestion question) {
        return QuestionRenderingHelper.syntaxHighlightForJava(DragAndDropRenderingHelper.substituteSlots(question, question.getSubstitutedExplanationText(), true));
    }

    /**
     * Replace <slot>fragment</slot> tags with their implementation as HTML; also add <form> tags to the beginning and
     * end of the body.
     * @param includeSlotValues TODO
     */
    private static String substituteSlots(DragAndDropQuestion question, String s, boolean includeSlotValues) {
        int idCounter = 0;
        Matcher matcher = SLOT_REGEX.matcher(s);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String slotFragment = includeSlotValues ? matcher.group(1) : "";
            String replacement = Matcher.quoteReplacement("<input size=\"" + question.largestFragmentWidth()
                    + "\" value=\"" + slotFragment + "\" id=\"" + idCounter + "\">");
    
            matcher.appendReplacement(buffer, replacement);
            idCounter++;
        }
        matcher.appendTail(buffer);
        String intermediate = buffer.toString();
    
        // Ensure there's a <form> at the start and end of the body
        // TODO: Handle this better
        Matcher m2 = Pattern.compile("<body>", Pattern.CASE_INSENSITIVE).matcher(intermediate);
        intermediate = m2.replaceAll("<body><form>");
        Matcher m3 = Pattern.compile("</body>", Pattern.CASE_INSENSITIVE).matcher(intermediate);
        intermediate = m3.replaceAll(Matcher.quoteReplacement("</form></body>"));
    
        return intermediate;
    }

}
