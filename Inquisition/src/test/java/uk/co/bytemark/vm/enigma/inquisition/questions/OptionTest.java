package uk.co.bytemark.vm.enigma.inquisition.questions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

public class OptionTest {

    private Option optionTrue;

    private Option optionFalse;

    @Before
    public void setUp() throws Exception {
        optionTrue = new Option("Option A", true, 1);
        optionFalse = new Option("Option B", false, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionAtNullOptionTextInConstructor() throws Exception {
        new Option(null, true, 1);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionAtNullElementInXmlConstructor() throws Exception {
        new Option(null, 1);
    }

    @Test
    public void toXmlAndBackAgainShouldProduceSameOption() throws Exception {
        assertEquals(optionTrue, new Option(optionTrue.asXML(), 1));
        assertEquals(optionFalse, new Option(optionFalse.asXML(), 2));
    }

    @Test
    public void asXmlShouldReturnCorrectXmlRepresentation() throws Exception {
        Element root = optionTrue.asXML();
        assertEquals("Option", root.getName());
        assertEquals(1, root.getAttributes().size());
        Attribute correctAttribute = root.getAttribute("correct");
        try {
            assertEquals(true, correctAttribute.getBooleanValue());
        } catch (DataConversionException e) {
            fail("Couldn't convert 'boolean' value");
        }
        assertEquals(0, root.getChildren().size());
        assertEquals(optionTrue.getOptionText(), root.getText());
    }

    @Test(expected = ParseException.class)
    public void shouldFailIfRootOfXmlHasWrongName() throws Exception {
        String xml = "<Optoin correct='true'>Foo</Optoin>";
        Element root = Utils.makeXMLFragment(xml);
        new Option(root, 1);
    }
}
