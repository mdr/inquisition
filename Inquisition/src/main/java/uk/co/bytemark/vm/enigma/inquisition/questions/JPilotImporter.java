/*
 * JPilotImporter.java
 *
 * Created on 27 September 2006, 12:33
 */

package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;

/**
 * An importer for JPilot exams.
 */

public class JPilotImporter extends AbstractQuestionSetImporter {
    private static final Logger LOGGER = Logger.getLogger( AbstractQuestionSetImporter.class.getName() );

    @Override
    protected QuestionSet processDocument( Document doc ) throws ParseException {
        String name = "";
        String description = "";
        String passPercentage = "";
        String instructions = "";
        String version = "";
        String author = "";
        String authorDescription = "";
        String authorEmail = "";
        String authorURL = "";
        int recommendedTime = 1000;
        List<Question> questions = new ArrayList<Question>();
        Element root = doc.getRootElement();

        if ( !root.getName().toLowerCase().equals( "exam" ) ) {
            throw new ParseException( "JPilotImporter: Root not <Exam>", 0 );
        }

        List<?> topElements = root.getChildren();
        for ( Object object : topElements ) {

            Element topElement = (Element) object;
            String tagName = topElement.getName().toLowerCase();

            if ( tagName.equals( "name" ) ) {
                name = topElement.getText();
            } else if ( tagName.equals( "description" ) ) {
                description = topElement.getText();
            } else if ( tagName.equals( "passpercentage" ) ) {
                passPercentage = topElement.getText();
            } else if ( tagName.equals( "instructions" ) ) {
                instructions = topElement.getText();
            } else if ( tagName.equals( "version" ) ) {
                version = topElement.getText();
            } else if ( tagName.equals( "sections" ) ) {
                continue;
            } else if ( tagName.equals( "title" ) ) {
                continue;
            } else if ( tagName.equals( "author" ) ) {
                author = topElement.getText();
            } else if ( tagName.equals( "authordescription" ) ) {
                authorDescription = topElement.getText();
            } else if ( tagName.equals( "authoremail" ) ) {
                authorEmail = topElement.getText();
            } else if ( tagName.equals( "authorurl" ) ) {
                authorURL = topElement.getText();
            } else if ( tagName.equals( "duration" ) ) {
                recommendedTime = Integer.parseInt( topElement.getText() );
            } else if ( tagName.equals( "questions" ) ) {

                for ( Object object2 : topElement.getChildren() ) { // Loop over questions
                    Element questionElement = (Element) object2;

                    String explanationText = "";
                    String questionText = "";
                    ArrayList<Option> options = new ArrayList<Option>();
                    int type = 1;
                    for ( Object object3 : questionElement.getChildren() ) { // Loop over question parts
                        Element questionPartElement = (Element) object3;
                        String name2 = questionPartElement.getName().toLowerCase();
                        if ( name2.equals( "explanation" ) ) {
                            explanationText = questionPartElement.getText();
                        } else if ( name2.equals( "problem" ) ) {
                            questionText = questionPartElement.getText();
                        } else if ( name2.equals( "type" ) ) {
                            type = Integer.parseInt( questionPartElement.getText() );
                        } else if ( name2.equals( "choices" ) ) {
                            for ( Object object4 : questionPartElement.getChildren() ) { // Loop over Choices
                                Element choiceElement = (Element) object4;
                                String optionText = "";
                                boolean optionCorrectness = false;
                                for ( Object object5 : choiceElement.getChildren() ) { // Loop over choice parts
                                    Element choicePartElement = (Element) object5;
                                    String name3 = choicePartElement.getName().toLowerCase();
                                    if ( name3.equals( "choicevalue" ) ) {
                                        optionText = "<html>" + choicePartElement.getText() + "</html>";
                                    } else if ( name3.equals( "choicevalidation" ) ) {
                                        optionCorrectness = Boolean.parseBoolean( choicePartElement.getText() );
                                    }
                                }
                                Option option = new Option( optionText, optionCorrectness, 1 );
                                options.add( option );

                            }

                        }
                    }
                    Question question;
                    if ( type == 2 ) {
                        LOGGER.warning( "JPilotImporter: Fill in the blank question found, skipping" );
                        continue;
                    } else if ( type == 1 ) {
                        question = new MultipleChoiceQuestion( questionText, explanationText, options, false, false );
                    } else if ( type == 0 ) {
                        question = new MultipleChoiceQuestion( questionText, explanationText, options, false, true );
                    } else {
                        LOGGER.warning( "JPilotImporter: Unrecognised type" );
                        continue;
                    }
                    questions.add( question );
                }

            } else {
                LOGGER.warning( "JPilotImporter: Unrecognised tag: " + tagName );
            }

        }
        String totalDescription = "<i>JPilot import</i><p>" + description + "<p>\n" + "Pass percentage: " + passPercentage + "<p>\n" + "Instructions: " + instructions + "<p>\n" + "Author: " + author + " &lt;" + authorEmail + "&gt;, " + authorDescription + ", " + authorURL + "<p>\n" + "Version: " + version + "<p>\n";
        QuestionSet questionSet = new QuestionSet( name, totalDescription, recommendedTime / questions.size(), "JPilot", questions );
        return questionSet;
    }
}
