//package uk.co.bytemark.vm.enigma.inquisition.questions.xml;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.fail;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.EnumSet;
//import java.util.List;
//
//import org.jdom.Attribute;
//import org.jdom.DataConversionException;
//import org.jdom.Element;
//import org.jdom.JDOMException;
//import org.junit.Before;
//import org.junit.Test;
//
//import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;
//import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
//import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
//
//public class XmlQuestionSetParserTestOld {
//
//    private XmlQuestionSetParser xmlQuestionSetParser;
//
//    @Test
//    public void shouldReturnCorrectXMLRepresentation() {
//        Element root = multipleOptionModeQuestion.asXML();
//        assertEquals("MultipleChoiceQuestion", root.getName());
//        checkXMLRepresentationRootElementAttributes(root);
//        assertEquals(3, root.getChildren().size());
//        checkXMLRepresentationQuestionAndExplanationElements(root);
//        checkXMLRepresentationOptionsElement(root);
//    }
//
//    @SuppressWarnings("unchecked")
//    // See below
//    private void checkXMLRepresentationOptionsElement(Element root) {
//        Element optionsElement = root.getChild("Options");
//        assertNotNull(optionsElement);
//        assertEquals(0, optionsElement.getAttributes().size());
//        List<Element> optionElements = optionsElement.getChildren(); // We trust the JDOM API
//        assertEquals(multipleOptionModeQuestion.numberOfOptions(), optionElements.size());
//        for (Element optionElement : optionElements)
//            assertEquals("Option", optionElement.getName());
//    }
//
//    private void checkXMLRepresentationQuestionAndExplanationElements(Element root) {
//        Element questionTextElement = root.getChild("QuestionText");
//        assertNotNull(questionTextElement);
//        assertEquals(0, questionTextElement.getAttributes().size());
//        assertEquals(0, questionTextElement.getChildren().size());
//        assertEquals(multipleOptionModeQuestion.getQuestionText(), questionTextElement.getText());
//        Element explanationTextElement = root.getChild("ExplanationText");
//        assertNotNull(explanationTextElement);
//        assertEquals(0, explanationTextElement.getAttributes().size());
//        assertEquals(0, explanationTextElement.getChildren().size());
//        assertEquals(multipleOptionModeQuestion.getExplanationText(), explanationTextElement.getText());
//    }
//
//    private void checkXMLRepresentationRootElementAttributes(Element root) {
//        assertEquals(2, root.getAttributes().size());
//        Attribute singleOptionModeAttribute = root.getAttribute("singleOptionMode");
//        try {
//            assertEquals(multipleOptionModeQuestion.isSingleOptionMode(), singleOptionModeAttribute.getBooleanValue());
//        } catch (DataConversionException e) {
//            fail("Couldn't convert 'boolean' value");
//        }
//        Attribute shufflableAttribute = root.getAttribute("shufflable");
//        try {
//            assertEquals(multipleOptionModeQuestion.isShufflable(), shufflableAttribute.getBooleanValue());
//        } catch (DataConversionException e) {
//            fail("Couldn't convert 'boolean' value");
//        }
//    }
//
//    @Test
//    public void toXmlAndBackAgainShouldResultInSameQuestion() throws Exception {
//        Question multipleQuestionAgain = new MultipleChoiceQuestion(multipleOptionModeQuestion.asXML());
//        assertEquals(multipleOptionModeQuestion, multipleQuestionAgain);
//        Question singleQuestionAgain = new MultipleChoiceQuestion(singleOptionModeQuestion.asXML());
//        assertEquals(singleOptionModeQuestion, singleQuestionAgain);
//    }
//
//    @Test
//    public void shouldThrowParseExceptionsAtDodgyXml() throws Exception {
//        // Some extra or missing stuff in the XML can be ignored, but the following really need
//        // to be present
//        Element questionElement = createXmlQuestion(EnumSet.of(OPTIONS, INNER_OPTIONS));
//        checkDodgyXmlThrowsParseException(questionElement);
//        questionElement = createXmlQuestion(EnumSet.of(QUESTION_TEXT));
//        checkDodgyXmlThrowsParseException(questionElement);
//        questionElement = createXmlQuestion(EnumSet.of(QUESTION_TEXT, OPTIONS));
//        checkDodgyXmlThrowsParseException(questionElement);
//    }
//
//    private void checkDodgyXmlThrowsParseException(Element questionElement) {
//        try {
//            new MultipleChoiceQuestion(questionElement);
//            fail("Should throw ParseException because of missing XML elements");
//        } catch (ParseException e) {
//            // Exception expected from test
//        }
//    }
//
//    private Element createXmlQuestion(EnumSet<MultipleChoiceQuestionComponent> components) throws JDOMException,
//            IOException {
//        String questionText = components.contains(QUESTION_TEXT) ? "<QuestionText>Question</QuestionText>" : "";
//        String innerOptions = components.contains(INNER_OPTIONS) ? "<Option correct='true' id='a'>Foo</Option>"
//                + "<Option correct='false' id='b'>Bar</Option>" : "";
//        String options = components.contains(OPTIONS) ? "<Options>" + innerOptions + "</Options>" : "";
//        String questionXML = "<MultipleChoiceQuestion shufflable='true' singleOptionMode='false'>" + questionText
//                + "<ExplanationText>Explanation</ExplanationText>" + options + "</MultipleChoiceQuestion>";
//        return Utils.makeXMLFragment(questionXML);
//    }
//
//    @Test(expected = ParseException.class)
//    public void shouldFailIfRootOfXmlHasWrongName() throws Exception {
//        String questionXML = "<SomeOtherQuestion shufflable='true' singleOptionMode='false'>"
//                + "<QuestionText>Question</QuestionText>" + "<ExplanationText>Explanation</ExplanationText>"
//                + "<Options>" + "<Option correct='true' id='a'>Foo</Option>" + "</Options>" + "</SomeOtherQuestion>";
//        Element questionRoot = Utils.makeXMLFragment(questionXML);
//        new MultipleChoiceQuestion(questionRoot);
//    }
//
//    @Before
//    public void setUp() throws Exception {
//        xmlQuestionSetParser = new XmlQuestionSetParser();
//    }
//
//}
