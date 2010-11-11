package uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.bytemark.vm.enigma.inquisition.questions.MultipleChoiceQuestion;
import uk.co.bytemark.vm.enigma.inquisition.questions.Option;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;

public class MockQuestionSetMother {
    private static final String NO_PACKAGE = "";

    public static final String QUESTION_SET_DESCRIPTION = "Question Set Description";

    private static final String DESCRIPTION = "<html><head></head><body>" + QUESTION_SET_DESCRIPTION + "</body></html>";

    public static List<QuestionSet> getInitialQuestionSets() {
        List<QuestionSet> questionSets = new ArrayList<QuestionSet>();
        questionSets.add(mockQuestionSet(name("Mock Questions 2"), inPackage("Mock Package 1")));
        questionSets.add(mockQuestionSet(name("Mock Questions 1"), inPackage("Mock Package 1")));
        questionSets.add(mockQuestionSet(name("Mock Questions 3"), inPackage("Mock Package 2")));
        questionSets.add(mockQuestionSet(name("Mock Questions 4"), NO_PACKAGE));
        return questionSets;
    }

    private static QuestionSet mockQuestionSet(String name, String packageSequence) {
        Question mockQuestion = new MultipleChoiceQuestion("QuestionText", "ExplanationText", Collections
                .singletonList(new Option("optionText", true, 1)), true, true);
        return new QuestionSet(name, DESCRIPTION, 3, packageSequence, Collections.singletonList(mockQuestion));
    }

    private static String inPackage(String inPackage) {
        return inPackage;
    }

    private static String name(String name) {
        return name;
    }

}
