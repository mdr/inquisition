/*
 * UltramockImporter.java
 * 
 * Created on 27 September 2006, 17:57
 */

package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;

/**
 * 
 * @author Matt
 */
public class UltramockImporter extends AbstractQuestionSetImporter {

    private static final Logger LOGGER = Logger.getLogger( UltramockImporter.class.getName() );

    @Override
    protected QuestionSet processDocument( Document doc ) throws ParseException {
        String title = "";
        String disclaimer = "";
        int recommendedTime = 1000;
        List<Question> questions = new ArrayList<Question>();
        Element root = doc.getRootElement();

        if ( !root.getName().toLowerCase().equals( "mock" ) ) {
            throw new ParseException( "Root not <mock>", 0 );
        }
        title = root.getAttribute( "name" ).getValue();
        List<?> topElements = root.getChildren();
        for ( Object object : topElements ) {

            Element topElement = (Element) object;
            String tagName = topElement.getName().toLowerCase();

            if ( tagName.equals( "info" ) ) {
                recommendedTime = Integer.parseInt( topElement.getAttributeValue( "time" ) );

                for ( Object object2 : topElement.getChildren() ) { // Loop over things in <info>
                    Element infoElement = (Element) object2;
                    String name = infoElement.getName().toLowerCase();
                    if ( name.equals( "disclaimer" ) ) {
                        disclaimer = infoElement.getText();
                    }
                }
            } else if ( tagName.equals( "problems" ) ) {

                for ( Object object2 : topElement.getChildren() ) { // Loop over questions
                    Element questionElement = (Element) object2;
                    String problemType = questionElement.getName().toLowerCase();
                    String explanationText = "";
                    String questionText = "";
                    String codeText = "";
                    List<String> optionStrings = new ArrayList<String>();
                    Set<Integer> correctOptions = new HashSet<Integer>();
                    for ( Object object3 : questionElement.getChildren() ) { // Loop over question
                        // parts
                        Element questionPartElement = (Element) object3;
                        String name2 = questionPartElement.getName().toLowerCase();
                        if ( name2.equals( "explanation" ) ) {
                            explanationText = questionPartElement.getText();
                        } else if ( name2.equals( "question" ) ) {
                            questionText = questionPartElement.getText();
                        } else if ( name2.equals( "code" ) ) {
                            codeText = questionPartElement.getText();
                        } else if ( name2.equals( "option" ) ) {
                            optionStrings.add( questionPartElement.getText() );
                        } else if ( name2.equals( "answer" ) ) {
                            correctOptions.add( Integer.parseInt( questionPartElement.getText() ) - 1 );
                        }
                    }
                    ArrayList<Option> options = new ArrayList<Option>();
                    for ( int i = 0; i < optionStrings.size(); i++ ) {
                        String s = optionStrings.get( i );
                        boolean correct = ( correctOptions.contains( i ) ? true : false );
                        Option option = new Option( s, correct, i + 1 );
                        options.add( option );
                    }
                    questionText = "<html>" + questionText + "<br><pre>" + codeText + "</pre></html>";
                    Question question;
                    if ( problemType.equals( "single-answer-problem" ) ) {
                        question = new MultipleChoiceQuestion( questionText, explanationText, options, false, true );
                    } else if ( problemType.equals( "multi-answer-problem" ) ) {
                        question = new MultipleChoiceQuestion( questionText, explanationText, options, false, false );
                    } else {
                        LOGGER.warning( "Unrecognised type" );
                        continue;
                    }
                    questions.add( question );
                }

            } else {
                // throw new ParseException("unrecognised tag" + tagName, 0);
                LOGGER.warning( "Unrecognised tag: " + tagName );
            }

        }
        QuestionSet questionSet = new QuestionSet( title, disclaimer, recommendedTime / questions.size(), "Ultramock", questions );
        return questionSet;
    }

}
