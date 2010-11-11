/*
 * QuestionSetImporter.java
 * 
 * Created on 16 September 2006, 11:04
 */

package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import uk.co.bytemark.vm.enigma.inquisition.questions.bundledquestions.BundledQuestions;

/**
 * 
 * @author Matt
 */
abstract class AbstractQuestionSetImporter implements QuestionSetImporter {
    /**
     * Constructs a <tt>QuestionSet</tt> from an <tt>InputStream<tt> where the stream provides
     * an XML description of the data.
     * @param stream an input stream from which to read XML data.
     * @throws org.jdom.JDOMException
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    public QuestionSet buildQuestionSet(InputStream inputStream) throws JDOMException, IOException, ParseException {

        Document doc = readStreamAndBuildFromXML(inputStream);
        return processDocument(doc);
    }

    /**
     * Subclasses override this to implement an XML file format reader
     */
    abstract QuestionSet processDocument(Document doc) throws ParseException;

    /**
     * Utility method to convert an inputStream into an XML Document
     */
    public static Document readStreamAndBuildFromXML(InputStream inputStream) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder(false); // true);
//        builder.setEntityResolver(new Utils.NoOpEntityResolver()); // ignore DTDs
         builder.setEntityResolver(new InquisitionDtdResolver());
        return builder.build(inputStream);
    }

    public static class InquisitionDtdResolver implements EntityResolver {
        private static final String INQUISITION_QUESTIONS_DTD = "inquisitionQuestions.dtd";

        public InputSource resolveEntity(String publicId, String systemId) {
            if (systemId != null && systemId.endsWith(INQUISITION_QUESTIONS_DTD)) {
                InputStream input = BundledQuestions.class.getResourceAsStream(INQUISITION_QUESTIONS_DTD);
                return new InputSource(input);
            } else {
                return new InputSource();
            }
        }
    }

}
