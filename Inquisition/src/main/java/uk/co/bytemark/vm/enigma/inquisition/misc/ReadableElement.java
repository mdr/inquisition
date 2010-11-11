/*
 * ReadableElement.java
 *
 * Created on 15 September 2006, 13:21
 */

package uk.co.bytemark.vm.enigma.inquisition.misc;

import org.jdom.CDATA;
import org.jdom.Element;
//import static java.util.regex.Pattern.quote;
/**
 *
 * @author Matt
 * Overrides setText to use CDATA instead if any escaping is needed
 * This makes the source file more readable
 */
public class ReadableElement extends Element {
    
    public ReadableElement(String text) {
        super(text);
    }
    
    @Override
    public Element setText(String text) {
        if (text.matches("(?s).*[<>/&'\"].*")) {
            return addContent(new CDATA(text));
        } else {
            return super.setText(text);
        }
    }
}
