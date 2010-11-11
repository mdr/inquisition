package uk.co.bytemark.vm.enigma.inquisition.questions;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.jdom.Document;
import org.junit.Test;

import uk.co.bytemark.vm.enigma.inquisition.questions.xml.XmlQuestionSetParser;

public class NativeQuestionSetImporterTest {

    @Test
    public void toAndFromXmlShouldHaveNoEffect() throws Exception {
        Collection<QuestionSet> bundledQuestionSets = QuestionSetManager.loadBundledQuestionSets();
        NativeQuestionSetImporter questionSetImporter = new NativeQuestionSetImporter();
        XmlQuestionSetParser questionSetParser = new XmlQuestionSetParser();
        for (QuestionSet questionSet : bundledQuestionSets) {
            Document xmlDocument = questionSetParser.asXmlDocument(questionSet);
            QuestionSet reparsedQuestionSet = questionSetImporter.processDocument(xmlDocument);
            assertEquals(questionSet, reparsedQuestionSet);
        }

    }
}
