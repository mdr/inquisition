package uk.co.bytemark.vm.enigma.inquisition.quiz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.co.bytemark.vm.enigma.inquisition.gui.misc.QuizConfig;
import uk.co.bytemark.vm.enigma.inquisition.misc.Constants;
import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropRenderingHelper;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestionInstance;
import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceRenderingHelper;
import uk.co.bytemark.vm.enigma.inquisition.questions.Option;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionRenderingHelper;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSetManager;

public class HTMLQuizRenderer {

    private final QuizConfig quizConfig;

    private final boolean showAnswers;

    private final boolean repeatMultipleChoiceQuestionInAnswer;

    private final boolean addLinksBetweenQuestionsAndAnswers;

    public HTMLQuizRenderer(QuizConfig quizConfig, boolean showAnswers, boolean repeatMultipleChoiceQuestionInAnswer,
            boolean addLinksBetweenQuestionsAndAnswers) {
        if (!showAnswers && (repeatMultipleChoiceQuestionInAnswer || addLinksBetweenQuestionsAndAnswers))
            throw new IllegalArgumentException("Invalid combination of rendering options");
        this.quizConfig = quizConfig;
        this.showAnswers = showAnswers;
        this.repeatMultipleChoiceQuestionInAnswer = repeatMultipleChoiceQuestionInAnswer;
        this.addLinksBetweenQuestionsAndAnswers = addLinksBetweenQuestionsAndAnswers;
    }

    public String asHtml(QuestionSet questionSet) {
        StringBuilder builder = new StringBuilder();

        String questionSetName = questionSet.getName();
        builder.append("<html><head><title>").append(questionSetName).append("</title></head>");
        builder.append("<body>");
        builder.append("<h1>").append(questionSetName).append("</h1>");
        builder.append("<h1>").append("Description").append("</h1>");
        builder.append(questionSet.getDescription());
        List<Question> questions = new ArrayList<Question>(questionSet.getQuestions());
        if (quizConfig.shouldShuffleQuestionOrder())
            Collections.shuffle(questions);
        builder.append("<h1>").append("Questions").append("</h1>");
        int questionCounter = 1;
        Map<MultipleChoiceQuestion, MultipleChoiceQuestionInstance> optionsMap = Utils.newHashMap();
        for (Question genericQuestion : questions) {
            builder.append("<h2>").append("<a name='question").append(questionCounter).append("'>");
            builder.append("Question ").append(questionCounter);
            builder.append("</a>").append("</h2>");
            if (genericQuestion instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion question = (MultipleChoiceQuestion) genericQuestion;
                MultipleChoiceQuestionInstance questionInstance = new MultipleChoiceQuestionInstance(question);
                optionsMap.put(question, questionInstance);
                builder.append(renderMultipleChoiceQuestion(question, questionInstance, false));
            } else if (genericQuestion instanceof DragAndDropQuestion) {
                DragAndDropQuestion question = (DragAndDropQuestion) genericQuestion;
                builder.append(renderDragAndDropQuestion(question, false));
            }
            if (showAnswers && addLinksBetweenQuestionsAndAnswers)
                builder.append("<p><a href='#answer").append(questionCounter).append("'>").append("Go to answer")
                        .append("</a>");
            questionCounter++;
        }
        if (showAnswers) {
            builder.append("<h1>").append("Answers").append("</h1>");
            questionCounter = 1;
            for (Question genericQuestion : questions) {
                builder.append("<h2>").append("<a name='answer").append(questionCounter).append("'>");
                builder.append("Answer To Question ").append(questionCounter);
                builder.append("</a>").append("</h2>");
                if (genericQuestion instanceof MultipleChoiceQuestion) {
                    MultipleChoiceQuestion question = (MultipleChoiceQuestion) genericQuestion;
                    MultipleChoiceQuestionInstance questionInstance = optionsMap.get(question);
                    builder.append(renderMultipleChoiceQuestion(question, questionInstance, true));
                } else if (genericQuestion instanceof DragAndDropQuestion) {
                    DragAndDropQuestion question = (DragAndDropQuestion) genericQuestion;
                    builder.append(renderDragAndDropQuestion(question, true));
                }
                if (addLinksBetweenQuestionsAndAnswers)
                    builder.append("<p><a href='#question").append(questionCounter).append("'>").append(
                            "Go to question").append("</a>");
                questionCounter++;
            }

        }
        builder
                .append("<hr><center><font size='-1'>Generated by <a href='http://enigma.vm.bytemark.co.uk/inquisition'>Inquisition v"
                        + Constants.VERSION + "</a></font></center>");
        builder.append("</body></html>");
        return builder.toString();
    }

    private String renderDragAndDropQuestion(DragAndDropQuestion question, boolean explanation) {
        StringBuilder builder = new StringBuilder();
        String questionText = DragAndDropRenderingHelper.getQuestionText(question, false);
        if (explanation)
            questionText = DragAndDropRenderingHelper.getExplanationText(question);
        builder.append(questionText);
        if (!explanation) {
            String optionsTitle = DragAndDropRenderingHelper.optionsTitle(question);
            builder.append("<br>" + optionsTitle + "<br>");
            List<String> fragments = question.getFragments();
            for (String fragment : fragments) {
                builder.append("<table border='1' bgcolor='#f4f4ff' cellspacing='0'><tr><td>" + fragment
                        + "</td></tr></table>");
            }
            builder.append("<p>");
        }
        return builder.toString();
    }

    private String renderMultipleChoiceQuestion(MultipleChoiceQuestion question,
            MultipleChoiceQuestionInstance questionInstance, boolean explanation) {
        StringBuilder builder = new StringBuilder();
        if (!explanation || repeatMultipleChoiceQuestionInAnswer) {
            String questionText = QuestionRenderingHelper.syntaxHighlightForJava(question.getSubstitutedQuestionText());
            builder.append(questionText);
            builder.append("<p>");
            String optionsTitle = MultipleChoiceRenderingHelper.optionsTitle(question, quizConfig
                    .shouldStateNumberOfOptionsNeededForMultipleChoice());
            builder.append(optionsTitle).append(":");
        }
        builder.append("<ol type='A'>");

        int optionsCounter = 0;
        for (Option option : questionInstance.getOrderedOptions()) {
            String optionText = option.getOptionText();
            boolean check = explanation ? option.isCorrect() : false;
            String checked = check ? "checked='true'" : "";
            builder.append("<li>").append("<input type='checkbox' ").append(checked).append("'> ").append(optionText)
                    .append("</li>");
            optionsCounter++;
        }
        builder.append("</ol>");
        if (explanation) {
            builder.append("<p>");
            builder.append(MultipleChoiceRenderingHelper.getExplanationText(questionInstance));
        }
        return builder.toString();
    }

    // private static class HtmlViewerFrame extends JFrame {
    // public HtmlViewerFrame(String html) {
    // JTextPane textPane = new JTextPane();
    // textPane.setContentType("text/html");
    // textPane.setText(html);
    // add(new JScrollPane(textPane), BorderLayout.CENTER);
    // setDefaultCloseOperation(EXIT_ON_CLOSE);
    // pack();
    // }
    // }

    public static void main(String[] args) {
        File rootDir = new File(System.getProperty("java.io.tmpdir"), "inquisition-quizzes");
        if (!rootDir.exists()) {
            boolean succeeded = rootDir.mkdir();
            if (!succeeded) {
                System.err.println("Could not create directory " + rootDir.getAbsolutePath());
                return;
            }
        }
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.WARNING);
        StringBuilder builder = new StringBuilder(
                "<html><body><h1>Inquisition Quizzes</h1>"
                        + "The following have been generated by <a href='http://enigma.vm.bytemark.co.uk/inquisition'>Inquisition</a>, an open-source "
                        + "mock exam engine.<ul>");
        HTMLQuizRenderer quizRenderer = makeHtmlRenderer();
        int count = 0;
        for (QuestionSet questionSet : QuestionSetManager.loadBundledQuestionSets()) {
            System.out.println("Processing " + questionSet.getName());
            String fileName = "quiz" + count + ".html";
            builder.append("<li><a href='").append(fileName).append("'>").append(questionSet.getCategorySequence())
                    .append(" -- ").append(questionSet.getName()).append("</a></li>");
            final String html = quizRenderer.asHtml(questionSet);
            try {
                File location = new File(rootDir, fileName);
                PrintWriter printWriter = new PrintWriter(location);
                printWriter.write(html);
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            count++;
        }
        builder.append("</ul></body></html>");
        File location = new File(rootDir, "index.html");
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(location);
            printWriter.write(builder.toString());
            printWriter.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Output index:");
        System.out.println("file://" + location.getAbsolutePath());

        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        // HtmlViewerFrame htmlViewer = new HtmlViewerFrame(html);
        // htmlViewer.setVisible(true);
        // }
        // });
    }

    private static HTMLQuizRenderer makeHtmlRenderer() {
        QuizConfig quizConfig = QuizConfig.createWithTimer(false, true, 30);
        boolean showAnswers = true;
        boolean repeatMultipleChoiceQuestionInAnswer = false;
        boolean addLinksBetweenQuestionsAndAnswers = true;
        HTMLQuizRenderer printableQuizCreator = new HTMLQuizRenderer(quizConfig, showAnswers,
                repeatMultipleChoiceQuestionInAnswer, addLinksBetweenQuestionsAndAnswers);
        return printableQuizCreator;
    }
}
