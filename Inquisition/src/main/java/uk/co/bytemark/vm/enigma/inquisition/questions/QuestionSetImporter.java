package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import org.jdom.JDOMException;

public interface QuestionSetImporter {

    QuestionSet buildQuestionSet(InputStream inputStream) throws JDOMException, IOException, ParseException;
    
    
    
}
