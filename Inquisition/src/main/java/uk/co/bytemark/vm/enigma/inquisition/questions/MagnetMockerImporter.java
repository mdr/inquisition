/*
 * MagnetMockerImporter.java
 * 
 * Created on 03 October 2006, 20:25
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;

import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

/**
 * An importer for Magnet Mocker exams.
 * 
 */
public class MagnetMockerImporter extends AbstractQuestionSetImporter {

    private static final Logger LOGGER = Logger.getLogger( MagnetMockerImporter.class.getName() );

    @Override
    protected QuestionSet processDocument( Document doc ) throws ParseException {
        String name = "";
        String author = "";
        String url = "";
        String email = "";
        String version = "";

        List<Question> questions = new ArrayList<Question>();
        Element root = doc.getRootElement();

        if ( !root.getName().toLowerCase().equals( "quiz" ) ) {
            throw new ParseException( "Root not <quiz>", 0 );
        }

        List<?> topElements = root.getChildren();
        for ( Object object : topElements ) {

            Element topElement = (Element) object;
            String tagName = topElement.getName().toLowerCase();

            if ( tagName.equals( "name" ) ) {
                name = topElement.getText();
            } else if ( tagName.equals( "author" ) ) {
                author = topElement.getText();
            } else if ( tagName.equals( "url" ) ) {
                url = topElement.getText();
            } else if ( tagName.equals( "email" ) ) {
                email = topElement.getText();
            } else if ( tagName.equals( "version" ) ) {
                version = topElement.getText();
            } else if ( tagName.equals( "questions" ) ) {

                questionLoop: for ( Object object2 : topElement.getChildren() ) { // Loop over
                    // questions
                    Element questionElement = (Element) object2;

                    String explanationText = "";
                    String questionText = "";
                    String questionCode = "";
                    String answer = "";
                    ArrayList<Option> temporaryOptions = new ArrayList<Option>();
                    Map<String, Option> idToOptionMap = new HashMap<String, Option>();
                    for ( Object object3 : questionElement.getChildren() ) { // Loop over question
                        // parts
                        Element questionPartElement = (Element) object3;
                        String name2 = questionPartElement.getName().toLowerCase();
                        if ( name2.equals( "questiontext" ) ) {
                            questionText = Utils.htmlEscape( questionPartElement.getText() );
                        } else if ( name2.equals( "questioncode" ) ) {
                            questionCode = Utils.htmlEscape( questionPartElement.getText() );
                        } else if ( name2.equals( "explanationtext" ) ) {
                            explanationText = Utils.htmlEscape( questionPartElement.getText() );
                        } else if ( name2.equals( "answer" ) ) {
                            answer = questionPartElement.getText();
                        } else if ( name2.equals( "ordered" ) ) {
                            boolean ordered = Utils.parseBoolean( questionPartElement.getText() );
                            if ( ordered ) {
                                LOGGER.warning( "MagnetMockerImporter: Cannot handle ordered option questions yet, skipping" );
                                continue questionLoop;
                            }
                        } else if ( name2.equals( "imagename" ) ) {
                            LOGGER.warning( "MagnetMockerImporter: Cannot handle questions with images yet, skipping" );
                            continue questionLoop;
                        } else if ( name2.equals( "options" ) ) {
                            int count = 1;
                            for ( Object object4 : questionPartElement.getChildren() ) { // Loop over
                                // options
                                Element optionElement = (Element) object4;
                                String optionText = "";
                                String name3 = optionElement.getName().toLowerCase();
                                if ( name3.equals( "option" ) ) {
                                    optionText = "<html><tt>" + optionElement.getText() + "</tt></html>";
                                    String optionID = optionElement.getAttributeValue( "optionID" );
                                    Option option = new Option( optionText, false, count );
                                    idToOptionMap.put( optionID, option );
                                    count++;
                                    temporaryOptions.add( option );
                                }
                            }

                        }

                    }
                    Question question;
                    Set<Option> trueOptions = new HashSet<Option>();
                    for ( String trueOptionId : answer.split( "," ) ) {
                        trueOptions.add( idToOptionMap.get( trueOptionId ) );
                    }
                    List<Option> options = new ArrayList<Option>();
                    for ( Option option : temporaryOptions ) {
                        boolean truth = trueOptions.contains( option );
                        Option newOption = new Option( option.getOptionText(), truth, option.getId() );
                        options.add( newOption );
                    }
                    question = new MultipleChoiceQuestion( "<pre>" + questionText + "</pre>" + "<pre>" + questionCode + "</pre>", "<pre>" + explanationText + "</pre>", options, false, false );
                    questions.add( question );

                }

            }

        }
        String totalDescription = "<i>Magnet Mocker import</i><p>" + "Author: " + author + " &lt;" + email + "&gt;<br>" + "URL: " + url + "<br>" + "Version: " + version;

        QuestionSet questionSet = new QuestionSet( name, totalDescription, 60, "Magnet Mocker", questions );
        return questionSet;
    }

}
