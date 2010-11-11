package uk.co.bytemark.vm.enigma.inquisition.questions.xml;

import java.text.ParseException;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;

import uk.co.bytemark.vm.enigma.inquisition.misc.ReadableElement;
import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.Option;
import uk.co.bytemark.vm.enigma.inquisition.questions.ParsingProblemRecorder;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion.Builder;

public class XmlQuestionSetParser {
    private static final int QUESTION_SET_EXPORT_VERSION = 4;

    public static class QuestionSetXML {
        private QuestionSetXML() { // Cannot instantiate class
        }

        public static final String TAG_QUESTION_SET                  = "QuestionSet";
        public static final String ATTRIBUTE_VERSION                 = "version";
        public static final String TAG_QUESTIONS                     = "Questions";
        public static final String TAG_RECOMMENDED_TIME_PER_QUESTION = "RecommendedTimePerQuestion";
        public static final String TAG_DESCRIPTION                   = "Description";
        public static final String TAG_NAME                          = "Name";
        public static final String TAG_CATEGORY                      = "Category";
    }

    public static class QuestionXML {
        private QuestionXML() {
        // Cannot instantiate class
        }

        public static final String TAG_QUESTION_TEXT    = "QuestionText";
        public static final String TAG_EXPLANATION_TEXT = "ExplanationText";
    }

    public static class MultipleChoiceQuestionXML {
        private MultipleChoiceQuestionXML() { // Cannot instantiate class

        }

        public static final String TAG_MULTIPLE_CHOICE_QUESTION = "MultipleChoiceQuestion";
        public static final String ATTRIBUTE_SHUFFLABLE         = "shufflable";
        public static final String ATTRIBUTE_SINGLE_OPTION_MODE = "singleOptionMode";
        public static final String TAG_OPTIONS                  = "Options";
        public static final String TAG_OPTION                   = "Option";

    }

    /**
     * Constructs a new <tt>MultipleChoiceQuestion</tt> from a JDOM XML Element
     * 
     * @param element
     *            the JDOM XML element to parse.
     * @param parsingProblemRecorder
     * @throws ParseException
     *             if any vital information can't be found.
     */
    public MultipleChoiceQuestion parseMultipleChoiceQuestion(Element element,
            ParsingProblemRecorder parsingProblemRecorder) throws ParseException {
        Utils.checkArgumentNotNull(element, "element");
        Builder builder = new Builder();

        if (!element.getName().equals(MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION)) {
            String message = "Outer tag must be " + MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION;
            parsingProblemRecorder.recordFatal(message);
            throw new ParseException(message, 0);
        }
        processXmlQuestionAttributes(element, builder, parsingProblemRecorder);

        for (Object object : element.getChildren())
            processXmlSubTag((Element) object, builder, parsingProblemRecorder);

        checkThatAllNecessaryInformationIsPresent(builder, parsingProblemRecorder);
        return builder.build();
    }

    private void processXmlQuestionAttributes(Element element, Builder builder,
            ParsingProblemRecorder parsingProblemRecorder) throws ParseException {
        String attributeValue = element.getAttributeValue(MultipleChoiceQuestionXML.ATTRIBUTE_SINGLE_OPTION_MODE);
        if (attributeValue == null) {
            String message = "No " + MultipleChoiceQuestionXML.ATTRIBUTE_SINGLE_OPTION_MODE + " attribute on "
                    + MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION + ", defaulting to "
                    + builder.isSingleOptionMode();
            parsingProblemRecorder.recordWarning(message);
        } else {
            builder.singleOptionMode(Utils.parseBoolean(attributeValue));
        }

        attributeValue = element.getAttributeValue(MultipleChoiceQuestionXML.ATTRIBUTE_SHUFFLABLE);
        if (attributeValue == null) {
            String message = "No " + MultipleChoiceQuestionXML.ATTRIBUTE_SHUFFLABLE + " attribute on "
                    + MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION + ", defaulting to "
                    + builder.isShufflable();
            parsingProblemRecorder.recordWarning(message);
        } else {
            builder.shufflable(Utils.parseBoolean(attributeValue));
        }
    }

    private void processXmlSubTag(Element subElement, Builder builder, ParsingProblemRecorder parsingProblemRecorder)
            throws ParseException {
        String name = subElement.getName();

        if (name.equalsIgnoreCase(QuestionXML.TAG_QUESTION_TEXT)) {
            builder.questionText(subElement.getText());
        } else if (name.equalsIgnoreCase(QuestionXML.TAG_EXPLANATION_TEXT)) {
            builder.explanationText(subElement.getText());
        } else if (name.equalsIgnoreCase(MultipleChoiceQuestionXML.TAG_OPTIONS)) {
            processXmlOptions(subElement, builder, parsingProblemRecorder);
        } else {
            parsingProblemRecorder.recordWarning("Unknown tag while parsing elements under <"
                    + MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION + ">, skipping: " + name);
        }
    }

    private void processXmlOptions(Element subElement, Builder builder, ParsingProblemRecorder parsingProblemRecorder)
            throws ParseException {
        int count = 1;
        for (Object child : subElement.getChildren()) {
            Element optionElement = (Element) child;
            if (optionElement.getName().equalsIgnoreCase(MultipleChoiceQuestionXML.TAG_OPTION)) {
                builder.option(new Option(optionElement, count));
                count++;
            } else {
                parsingProblemRecorder.recordWarning("Unknown tag while parsing elements under <"
                        + MultipleChoiceQuestionXML.TAG_OPTIONS + ">, skipping: " + optionElement.getName());
            }
        }
    }

    private void checkThatAllNecessaryInformationIsPresent(Builder builder,
            ParsingProblemRecorder parsingProblemRecorder) throws ParseException {
        if (builder.getExplanationText() == null) { // We can live without an explanation, but flag it
            parsingProblemRecorder.recordWarning(QuestionXML.TAG_EXPLANATION_TEXT + " for "
                    + MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION + " missing");
            builder.explanationText("");
        }

        if (builder.getQuestionText() == null) {
            String message = "No questionText found in " + MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION + "";
            parsingProblemRecorder.recordFatal(message);
            throw new ParseException(message, 0);
        }
        if (builder.getOptions().size() == 0) {
            String message = "No options found in " + MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION + "";
            parsingProblemRecorder.recordFatal(message);
            throw new ParseException(message, 0);
        }

        int correctOptions = MultipleChoiceQuestion.countCorrectOptions(builder.getOptions());
        if (correctOptions == 0) {
            String message = "No correct options found in " + MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION
                    + "";
            parsingProblemRecorder.recordFatal(message);
            throw new ParseException(message, 0);
        }

        if (builder.isSingleOptionMode() && correctOptions > 1) {
            parsingProblemRecorder.recordError("Inconsistent question: "
                    + MultipleChoiceQuestionXML.ATTRIBUTE_SINGLE_OPTION_MODE + ", yet " + correctOptions
                    + " correct options given." + " Deactivating "
                    + MultipleChoiceQuestionXML.ATTRIBUTE_SINGLE_OPTION_MODE + " for this question.");
            builder.singleOptionMode(false);
        }
    }

    public Element asXML(MultipleChoiceQuestion question) {
        ReadableElement element = new ReadableElement(MultipleChoiceQuestionXML.TAG_MULTIPLE_CHOICE_QUESTION);

        // Add attributes
        element.setAttribute(MultipleChoiceQuestionXML.ATTRIBUTE_SHUFFLABLE, Boolean.toString(question.isShufflable()));
        element.setAttribute(MultipleChoiceQuestionXML.ATTRIBUTE_SINGLE_OPTION_MODE, Boolean.toString(question
                .isSingleOptionMode()));

        // Create sub-tags
        ReadableElement questionTextElement = new ReadableElement(QuestionXML.TAG_QUESTION_TEXT);
        questionTextElement.setText(question.getQuestionText());

        ReadableElement explanationTextElement = new ReadableElement(QuestionXML.TAG_EXPLANATION_TEXT);
        explanationTextElement.setText(question.getExplanationText());

        ReadableElement optionsElement = new ReadableElement(MultipleChoiceQuestionXML.TAG_OPTIONS);
        for (Option option : question.getOptions())
            optionsElement.addContent(option.asXML());

        // Add sub-tags
        element.addContent(questionTextElement);
        element.addContent(optionsElement);
        element.addContent(explanationTextElement);

        return element;
    }

    public Document asXmlDocument(QuestionSet questionSet) {
        Document doc = new Document(new XmlQuestionSetParser().asXML(questionSet));
        doc.setDocType(new DocType("QuestionSet", "inquisitionQuestions.dtd"));
        return doc;
    }

    public Element asXML(QuestionSet questionSet) {
        Element element = new Element(QuestionSetXML.TAG_QUESTION_SET);
        element.setAttribute(QuestionSetXML.ATTRIBUTE_VERSION, Integer.toString(QUESTION_SET_EXPORT_VERSION));

        ReadableElement nameElement = new ReadableElement(QuestionSetXML.TAG_NAME);
        nameElement.setText(questionSet.getName());
        element.addContent(nameElement);

        ReadableElement descriptionElement = new ReadableElement(QuestionSetXML.TAG_DESCRIPTION);
        descriptionElement.setText(questionSet.getDescription());
        element.addContent(descriptionElement);

        Element recommendedTimeElement = new Element(QuestionSetXML.TAG_RECOMMENDED_TIME_PER_QUESTION);
        recommendedTimeElement.setText(Integer.toString(questionSet.getRecommendedTimePerQuestion()));
        element.addContent(recommendedTimeElement);

        Element categoryElement = new Element(QuestionSetXML.TAG_CATEGORY);
        categoryElement.setText(questionSet.getCategorySequence());
        element.addContent(categoryElement);

        Element questionsElement = new Element(QuestionSetXML.TAG_QUESTIONS);
        for (Question question : questionSet.getQuestions()) {
            Element questionElement;
            if (question instanceof MultipleChoiceQuestion)
                questionElement = asXML((MultipleChoiceQuestion) question);
            else if (question instanceof DragAndDropQuestion)
                questionElement = ((DragAndDropQuestion) question).asXML();
            else
                throw new IllegalArgumentException("Unknown question type " + question.getClass().getName());
            questionsElement.addContent(questionElement);
        }
        element.addContent(questionsElement);
        return element;
    }
}
