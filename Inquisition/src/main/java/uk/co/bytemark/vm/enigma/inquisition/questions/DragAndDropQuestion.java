/*
 * DragAndDropQuestion.java
 * 
 * Created on 11 September 2006, 11:01
 */

package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import uk.co.bytemark.vm.enigma.inquisition.misc.ReadableElement;
import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * A "drag-and-drop" question. The question is posed as an HTML document containing empty text input boxes (the <i>slots</i>),
 * and a set of simple strings (<i>fragments</i>) to be placed in the slots. The user will be able to assign the
 * fragments to the slots to complete the question. It is not necessary for all the fragments to be used, nor for all
 * the slots to be filled. The correct answer is defined in the HTML question by the <tt>value</tt> attributes set on
 * the text input boxes. All the contents of the <tt>value</tt> tags will be used as fragments, together with any
 * extra fragments specified.
 * 
 * @see DragAndDropAnswer
 */
public class DragAndDropQuestion extends AbstractQuestion {

    private static final Logger  LOGGER       = Logger.getLogger(DragAndDropQuestion.class.getName());

    private final boolean        reuseFragments;

    private final List<String>   extraFragments;

    private static final Pattern SLOT_PATTERN = Pattern.compile("<slot>(.*?)</slot>", Pattern.DOTALL
                                                      | Pattern.CASE_INSENSITIVE);

    @Override
    public String getQuestionTypeName() {
        return "Drag and drop";
    }

    /**
     * Creates a new instance of DragAndDropQuestion
     * 
     * @param questionText
     *            HTML text to be used to pose this question. Any text-type <tt>INPUT</tt> tags will be parsed
     *            specially, turning them into ''slots'' and using their <tt>value</tt> attributes as fragments.
     * @param explanationText
     *            HTML text to be used to indicate and explain the correct answer to this question.
     * @param extraFragments
     *            a list of extra fragments to supplement those given in the <tt>questionText</tt>.
     * @param reuseFragments
     *            whether to let the user use one fragment in multiple slots.
     */
    public DragAndDropQuestion(String questionText, String explanationText, List<String> extraFragments,
            boolean reuseFragments) {
        super(questionText, explanationText);
        Utils.checkArgumentNotNull(extraFragments, "extraFragments");
        Preconditions.checkContentsNotNull(extraFragments, "No nulls allowed in extraFragments");
        checkForNestedSlotTags(questionText);
        checkForNestedSlotTags(explanationText);
        this.extraFragments = ImmutableList.copyOf(extraFragments);
        this.reuseFragments = reuseFragments;
    }

    /**
     * Constructs a new <tt>DragAndDropQuestion</tt> from a JDOM XML Element
     * 
     * @param element
     *            the JDOM XML element to parse;
     * @throws ParseException
     *             if any vital information can't be found.
     */
    DragAndDropQuestion(Element element) throws ParseException {
        extraFragments = new ArrayList<String>();

        // Get data from attributes
        String attributeValue = element.getAttributeValue("reuseFragments");
        if (attributeValue == null) {
            reuseFragments = true;
            LOGGER.warning("No reuseFragments attribute on DragAndDropQuestion, defaulting to " + reuseFragments);
        } else {
            reuseFragments = Utils.parseBoolean(attributeValue);
        }

        // Get data from sub-tags
        for (Object object : element.getChildren()) {
            Element subElement = (Element) object;
            String name = subElement.getName();

            if (name.equalsIgnoreCase("QuestionText")) {
                questionText = subElement.getText();

            } else if (name.equalsIgnoreCase("ExplanationText")) {
                explanationText = subElement.getText();

            } else if (name.equalsIgnoreCase("ExtraFragments")) {

                // Loop over the extra fragments
                for (Object object2 : subElement.getChildren()) {
                    Element fragmentElement = (Element) object2;
                    if (fragmentElement.getName().equalsIgnoreCase("Fragment")) {
                        String fragment = fragmentElement.getText();
                        extraFragments.add(fragment);
                    } else {
                        LOGGER.warning("Unknown tag while parsing elements under <ExtraFragments>, skipping: " + name);
                    }
                }
            } else { // Simply drop anything else quietly:
                LOGGER.warning("Unknown tag while parsing elements under <DragAndDropQuestion>, skipping: " + name);
            }
        }

        // Check to see if everything is there
        if (explanationText == null) { // We can live without an explanation, but flag it
            LOGGER.warning("ExplanationText for DragAndDropQuestion missing");
            explanationText = "";
        }
        if (questionText == null) // Little point in using this question if this is the case
            throw new ParseException("No questionText found in DragAndDropQuestion", 0);

        if (getFragments().size() == 0)
            throw new ParseException("No fragments found in DragAndDropQuestion", 0);

    }

    public Element asXML() {
        Element element = new Element("DragAndDropQuestion");

        // Add attributes
        element.setAttribute("reuseFragments", Boolean.toString(reuseFragments));

        // Create sub-tags
        ReadableElement questionTextElement = new ReadableElement("QuestionText");
        questionTextElement.setText(questionText);

        ReadableElement explanationTextElement = new ReadableElement("ExplanationText");
        explanationTextElement.setText(explanationText);

        Element fragmentListElement = new Element("ExtraFragments");
        for (String fragment : extraFragments) {
            ReadableElement fragmentElement = new ReadableElement("Fragment");
            fragmentElement.setText(fragment);
            fragmentListElement.addContent(fragmentElement);
        }

        // Add sub-tags
        element.addContent(questionTextElement);
        element.addContent(fragmentListElement);
        element.addContent(explanationTextElement);
        return element;
    }

    /**
     * Returns a list of all the fragments used by the question. This includes all the <tt>value</tt>s of the HTML
     * INPUT tags in the question text as well as any extra fragments specified.
     */
    public List<String> getFragments() {
        List<String> fragments = new ArrayList<String>(extraFragments);
        fragments.addAll(getCorrectFragments());

        // If fragments are reusable, then there's no point in maintaining
        // distinct equal fragments
        if (reuseFragments) {
            Set<String> fragmentSet = new HashSet<String>(fragments);
            fragments = new ArrayList<String>(fragmentSet);
        }

        return fragments;
    }

    /**
     * Returns a list of the extra fragments used in the question.
     */
    public List<String> getExtraFragments() {
        return Collections.unmodifiableList(extraFragments);
    }

    /**
     * Returns a list of all the fragments that can be derived from the question text.
     */
    public List<String> getCorrectFragments() {
        List<String> fragments = new ArrayList<String>();
        Matcher matcher = SLOT_PATTERN.matcher(questionText);
        while (matcher.find())
            fragments.add(matcher.group(1));
        return fragments;
    }

    private void checkForNestedSlotTags(String text) {
        Matcher matcher = SLOT_PATTERN.matcher(questionText);
        while (matcher.find()) {
            String fragment = matcher.group(1);
            if (fragment.matches(".*<[Ss][Ll][Oo][Tt]>.*"))
                throw new IllegalArgumentException("Nested <slot> tag");
        }
    }

    /**
     * Returns whether fragments can be reused.
     */
    public boolean canReuseFragments() {
        return reuseFragments;
    }

    /**
     * Returns the length of the largest fragment
     */
    public int largestFragmentWidth() {
        List<String> fragments = getFragments();
        int max = 1;
        for (String fragment : fragments)
            max = Math.max(max, fragment.length());
        return max;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((extraFragments == null) ? 0 : extraFragments.hashCode());
        result = prime * result + (reuseFragments ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DragAndDropQuestion other = (DragAndDropQuestion) obj;
        if (extraFragments == null) {
            if (other.extraFragments != null)
                return false;
        } else if (!extraFragments.equals(other.extraFragments))
            return false;
        if (reuseFragments != other.reuseFragments)
            return false;
        return true;
    }

    /**
     * Returns whether this is considered a complete answer for its <tt>DragAndDropQuestion</tt>.
     * 
     * @return <tt>true</tt> if at least one slot is filled; <tt>false</tt> otherwise.
     */
    public boolean isAnswered(Answer generalAnswer) {
        for (String slotAnswer : getDragAndDropAnswer(generalAnswer).getSlotAnswers())
            if (!slotAnswer.equals(""))
                return true;
        return false;

    }

    private DragAndDropAnswer getDragAndDropAnswer(Answer generalAnswer) {
        Utils.checkArgumentNotNull(generalAnswer, "answer");
        DragAndDropAnswer answer;
        try {
            answer = (DragAndDropAnswer) generalAnswer;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("answer must be a " + DragAndDropAnswer.class.getSimpleName()
                    + ", but was passed in a " + generalAnswer.getClass().getSimpleName());
        }
        return answer;
    }

    public Answer initialAnswer() {
        List<String> emptyStrings = Collections.nCopies(getCorrectFragments().size(), "");
        return new DragAndDropAnswer(emptyStrings);
    }

    public boolean isCorrect(Answer answer) {
        return ((DragAndDropAnswer) answer).getSlotAnswers().equals(getCorrectFragments());
    }

}