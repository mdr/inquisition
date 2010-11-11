/*
 * Question.java
 * 
 * Created on 29 August 2006, 10:32
 * 
 */
package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

/**
 * <tt>Question</tt> is an abstract base class for exam questions of various types.
 */
public abstract class AbstractQuestion implements Serializable, Question {

    private static final String  COPY_FROM_QUESTION_TAG      = "<CopyFromQuestion/>";

    protected String             questionText;

    protected String             explanationText;

    private static final Pattern COPY_TO_EXPLANATION_PATTERN = Pattern.compile(
                                                                     "<CopyToExplanation>(.*?)</CopyToExplanation>",
                                                                     Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public static String removeCopyToExplanationBlocks(String questionText) {
        Matcher matcher = COPY_TO_EXPLANATION_PATTERN.matcher(questionText);
        return matcher.replaceAll("$1");
    }

    /**
     * Creates a new instance of Question
     * 
     * @param questionText
     *            HTML text to be used to pose this question.
     * @param explanationText
     *            HTML text to be used to indicate and explain the correct answer to this question.
     */
    protected AbstractQuestion(String questionText, String explanationText) {
        Utils.checkArgumentNotNull(questionText, "questionText");
        Utils.checkArgumentNotNull(explanationText, "explanationText");

        if (questionText == null || explanationText == null)
            throw new NullPointerException("Arguments must be non-null");
        this.questionText = questionText;
        this.explanationText = explanationText;
    }

    protected AbstractQuestion() {
    // TODO: Get rid of this
    }

    /**
     * Returns the HTML text to be used to pose this question.
     */
    public String getQuestionText() {
        return questionText;
    }

    public String getSubstitutedQuestionText() {
        return removeCopyToExplanationBlocks(questionText);
    }

    /**
     * Returns the HTML text to be used to indicate and explain the correct answer to this question.
     */
    public String getExplanationText() {
        return explanationText;
    }

    /**
     * Give a name for questions of this type. Should return a short string that can preface "question", for example,
     * "Multiple choice". This is called by <tt>toString()</tt>.
     * 
     * @return a short name for questions of this type.
     */
    public abstract String getQuestionTypeName();

    @Override
    public String toString() {
        return getQuestionTypeName() + " question: \""
                + questionText.subSequence(0, Math.min(0 + 65, questionText.length())) + "...\"";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((explanationText == null) ? 0 : explanationText.hashCode());
        result = prime * result + ((questionText == null) ? 0 : questionText.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractQuestion other = (AbstractQuestion) obj;
        if (explanationText == null) {
            if (other.explanationText != null)
                return false;
        } else if (!explanationText.equals(other.explanationText))
            return false;
        if (questionText == null) {
            if (other.questionText != null)
                return false;
        } else if (!questionText.equals(other.questionText))
            return false;
        return true;
    }

    /**
     * Returns the explanation text, with any <CopyFromQuestion> tags in the explanation replaced with the contents of
     * the <CopyToExplanation> tag in the question
     */
    public String getSubstitutedExplanationText() {
        return substituteExplanationText(questionText, explanationText);
    }

    public static String substituteExplanationText(String questionText, String explanationText) {
        return explanationText.replace(COPY_FROM_QUESTION_TAG, getQuestionSectionToCopy(questionText));

    }

    private static String getQuestionSectionToCopy(String questionText) {
        Matcher matcher = COPY_TO_EXPLANATION_PATTERN.matcher(questionText);
        String match = null;
        while (matcher.find()) {
            if (match == null)
                match = matcher.group(1);
            else
                return "ERROR: Multiple <CopyToExplanation> tags in question text";
            // TODO: Check for this in constructor instead / as well
        }
        if (match == null)
            return "ERROR: No CopyToExplanation tag found";
        else
            return match;

    }
}
