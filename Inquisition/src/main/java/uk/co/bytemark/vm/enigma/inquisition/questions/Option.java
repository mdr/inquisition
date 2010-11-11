/*
 * Option.java
 * 
 * Created on 29 August 2006, 11:09
 */

package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.io.Serializable;
import java.text.ParseException;

import org.jdom.Element;

import uk.co.bytemark.vm.enigma.inquisition.misc.ReadableElement;
import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

/**
 * An option in a {@link MultipleChoiceQuestion}.
 * 
 */
public class Option implements Serializable {

    private static final String XML_ATTRIBUTE_CORRECT = "correct";

    private static final String XML_TAG_OPTION = "Option";

    private final String optionText;

    private final boolean correct;

    private final int id;

    /**
     * Constructs a new <tt>Option</tt>.
     * 
     * @param optionText
     *            the (HTML) text presented to the user as the option.
     * @param correct
     *            whether this option is correct.
     * @param id
     *            a label so that the option can be referenced from the question explanation.
     */
    public Option(String optionText, boolean correct, int id) {
        Utils.checkArgumentNotNull(optionText, "optionText");
        Utils.checkArgumentNotNull(id, "id");
        this.optionText = optionText;
        this.correct = correct;
        this.id = id;
    }

    /**
     * Constructs a new <tt>Option</tt> from a JDOM XML Element
     * 
     * @param element
     *            the JDOM XML element to parse.
     * @throws ParseException
     *             if any vital information can't be found.
     */
    public Option(Element element, int id) throws ParseException {
        this.id = id;
        if (!element.getName().equals(XML_TAG_OPTION))
            throw new ParseException(XML_TAG_OPTION + " tag expected", 0);
        optionText = element.getText();
        String correctAttributeValue = element.getAttributeValue(XML_ATTRIBUTE_CORRECT);
        if (correctAttributeValue == null)
            throw new ParseException("No 'correct' attribute on <" + XML_TAG_OPTION + ">", 0);
        correct = Utils.parseBoolean(correctAttributeValue);
    }

    /**
     * Returns a JDOM XML <tt>Element</tt> representing this option.
     */
    public Element asXML() {
        ReadableElement element = new ReadableElement(XML_TAG_OPTION);
        element.setAttribute(XML_ATTRIBUTE_CORRECT, Boolean.toString(correct));
        element.setText(optionText);
        return element;
    }

    public String getOptionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (correct ? 1231 : 1237);
        result = prime * result + id;
        result = prime * result + ((optionText == null) ? 0 : optionText.hashCode());
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
        final Option other = (Option) obj;
        if (correct != other.correct)
            return false;
        if (id != other.id)
            return false;
        if (optionText == null) {
            if (other.optionText != null)
                return false;
        } else if (!optionText.equals(other.optionText))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Option(" + optionText + "," + correct + "," + id + ")";
    }

}
