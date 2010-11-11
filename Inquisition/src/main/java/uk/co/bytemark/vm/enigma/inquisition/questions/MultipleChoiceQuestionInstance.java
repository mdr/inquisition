package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

public class MultipleChoiceQuestionInstance {

    private final MultipleChoiceQuestion question;

    private final List<Option> orderedOptions;

    public MultipleChoiceQuestionInstance(MultipleChoiceQuestion question) {
        Utils.checkArgumentNotNull(question, "question");
        this.question = question;
        List<Option> options_ = new ArrayList<Option>(question.getOptions());
        if (question.isShufflable())
            Collections.shuffle(options_);
        this.orderedOptions = options_;
    }

    public List<Option> getOrderedOptions() {
        return Collections.unmodifiableList(orderedOptions);
    }

    public MultipleChoiceQuestion getQuestion() {
        return question;
    }
    
}
