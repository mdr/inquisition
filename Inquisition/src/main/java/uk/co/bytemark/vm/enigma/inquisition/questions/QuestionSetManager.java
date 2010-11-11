package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.JDOMException;

import uk.co.bytemark.vm.enigma.inquisition.misc.InputStreamDuplicator;
import uk.co.bytemark.vm.enigma.inquisition.questions.bundledquestions.BundledQuestions;

public class QuestionSetManager {

    private static final Logger LOGGER = Logger.getLogger(QuestionSetManager.class.getName());

    private static final List<QuestionSetImporter> IMPORTERS;
    static {
        List<QuestionSetImporter> importerList = new ArrayList<QuestionSetImporter>();
        importerList.add(new NativeQuestionSetImporter());
        importerList.add(new MagnetMockerImporter());
        importerList.add(new UltramockImporter());
        importerList.add(new JPilotImporter());
        IMPORTERS = Collections.unmodifiableList(importerList);
    }

    public static Collection<QuestionSet> loadBundledQuestionSets() {
        LOGGER.info("Loading bundled question sets");
        Collection<QuestionSet> bundledQuestionSets = new ArrayList<QuestionSet>();
        for (String fileName : BundledQuestions.BUNDLED_QUIZ_FILE_NAMES) {
            LOGGER.info("Importing bundled question set " + fileName);
            QuestionSet questionSet;
            InputStream stream = null;
            try {
                stream = BundledQuestions.class.getResourceAsStream(fileName);
                if (stream == null) {
                    LOGGER.warning("Could not open bundled question set file: " + fileName);
                } else {
                    questionSet = getQuestionSetFromInputStream(stream);
                    bundledQuestionSets.add(questionSet);
                }
            } catch (ParseException e) {
                LOGGER.log(Level.WARNING, "Could not parse bundled question set: " + fileName, e);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not open bundled question set: " + fileName, e);
            } finally {
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Could not close stream", e);
                }
            }
        }
        return bundledQuestionSets;
    }

    /*
     * Attempts to open an input stream using various importers
     */
    public static QuestionSet getQuestionSetFromInputStream(InputStream givenStream) throws IOException, ParseException {
        InputStreamDuplicator duplicator = new InputStreamDuplicator(givenStream);

        Exception exception = new RuntimeException("Start of exceptions");
        for (QuestionSetImporter importer : IMPORTERS) {
            InputStream stream = duplicator.freshCopy();
            try {
                return importer.buildQuestionSet(stream);
            } catch (ParseException e) {
                e.initCause(exception);
                exception = e;
            } catch (JDOMException e) {
                e.initCause(exception);
                exception = e;
            }
        }
        ParseException finalException = new ParseException("Unknown or invalid format", 0);
        finalException.initCause(exception);
        throw finalException;
    }

    // private Collection<QuestionSet> questionSets;
    //
    // public Collection<QuestionSet> getQuestionSets() {
    // return Collections.unmodifiableCollection(questionSets);
    // }
    //
    // public QuestionSetManager(Collection<QuestionSet> questionSets) {
    // this.questionSets = new ArrayList<QuestionSet>(questionSets);
    // }

}
