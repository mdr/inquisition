package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;

import uk.co.bytemark.vm.enigma.inquisition.questions.xml.XmlQuestionSetParser;

/**
 * Importer for the native file format
 * 
 */
public class NativeQuestionSetImporter extends AbstractQuestionSetImporter {
    private static final Logger LOGGER = Logger.getLogger( NativeQuestionSetImporter.class.getName() );

    /**
     * Constructs a new <tt>QuestionSet</tt> from a JDOM Document
     * 
     * @param doc
     *            the JDOM document element to parse.
     * @throws ParseException
     *             if any vital information can't be found.
     */
    @Override
    public QuestionSet processDocument( Document doc ) throws ParseException {
        Element root = doc.getRootElement();

        if ( !root.getName().equalsIgnoreCase( "QuestionSet" ) )
            throw new ParseException( "QuestionSet: Root not <QuestionSet>", 0 );

        // Check version number
        String versionString = root.getAttributeValue( "version" );
        if ( versionString == null ) {
            LOGGER.warning( "No `version' attribute on <QuestionSet> element" );
        } else {
            try {
                int version = Integer.parseInt( versionString );
                if ( version == 1 || version == 2 || version == 3 ) {
                    LOGGER.warning( "QuestionSet version " + version + " is no longer " + "supported: proceeding anyway, but errors may be present." );
                } else if ( version == 4 ) {
                    // Supported, do nothing
                } else {
                    // Unsupported, give warning
                    LOGGER.warning( "QuestionSet format version (" + version + ") is newer than this software and may not be handled correctly." );
                }
            } catch ( NumberFormatException e ) {
                LOGGER.log( Level.WARNING, "Cannot parse version number: " + versionString, e );
            }
        }

        List<?> topElements = root.getChildren();

        // Loop over the top-level elements
        String name = null;
        String description = null;
        int recommendedTimePerQuestion = -1;
        String category = "";
        List<Question> questions = new ArrayList<Question>();

        for ( Object object : topElements ) {

            Element topElement = (Element) object;
            String tagName = topElement.getName();

            if ( tagName.equalsIgnoreCase( "Name" ) ) {
                name = topElement.getText();

            } else if ( tagName.equalsIgnoreCase( "Description" ) ) {
                description = topElement.getText();

            } else if ( tagName.equalsIgnoreCase( "RecommendedTimePerQuestion" ) ) {
                recommendedTimePerQuestion = Integer.parseInt( topElement.getText() );

            } else if ( tagName.equalsIgnoreCase( "Category" ) ) {
                category = topElement.getText();

            } else if ( tagName.equalsIgnoreCase( "Questions" ) ) {

                // Loop over each question
                for ( Object object2 : topElement.getChildren() ) {
                    Element questionElement = (Element) object2;
                    String name2 = questionElement.getName();
                    try {
                        if ( name2.equalsIgnoreCase( "MultipleChoiceQuestion" ) ) {
                            questions.add( new XmlQuestionSetParser().parseMultipleChoiceQuestion( questionElement, new ParsingProblemRecorder() ) );

                        } else if ( name2.equalsIgnoreCase( "DragAndDropQuestion" ) ) {
                            questions.add( new DragAndDropQuestion( questionElement ) );
                        } else {
                            LOGGER.warning( "Unrecognised tag: " + name2 );
                        }
                    } catch ( ParseException e ) {
                        LOGGER.log( Level.WARNING, "Error parsing Question, skipping", e );
                        continue;
                    }

                }
            } else {
                LOGGER.warning( "Unrecognised tag: " + tagName );
            }
        }
        if ( questions.size() == 0 )
            throw new ParseException( "No valid questions found in QuestionSet", 0 );

        if ( name == null ) {
            LOGGER.warning( "no <Name> provided" );
            name = "No name given";
        }
        if ( recommendedTimePerQuestion == -1 ) {
            LOGGER.warning( "no <RecommendedTimePerQuestion> provided" );
            recommendedTimePerQuestion = 120; // default two minutes per question
        }

        if ( category == "" ) {
            LOGGER.warning( "No category listed for this question set" );
        }

        if ( description == null ) {
            LOGGER.warning( "no <Description> provided" );
            description = "No description given";
        }

        return new QuestionSet( name, description, recommendedTimePerQuestion, category, questions );

    }

}
