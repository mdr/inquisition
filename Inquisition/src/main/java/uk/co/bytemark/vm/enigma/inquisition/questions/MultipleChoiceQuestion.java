/*
 * MultipleChoiceQuestion.java
 * 
 * Created on 29 August 2006, 10:41
 */

package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

/**
 * A multiple choice question, of either the "choose all that apply" or the "choose one answer" variety (aka "single
 * option mode").
 * 
 * For either type, at least one option should be correct.
 * 
 * @see MultipleChoiceAnswer
 */
public class MultipleChoiceQuestion extends AbstractQuestion {

    private final List<Option> options;
    private final boolean      shufflable;
    private final boolean      singleOptionMode;

    /**
     * Constructs a new <tt>MultipleChoiceQuestion</tt>
     * 
     * @param questionText
     *            HTML text to be used to pose this question.
     * @param explanationText
     *            HTML text to be used to indicate and explain the correct answer to this question. Some special tags
     *            are used to substitute option labels. These are:
     *            <ul>
     * <li><tt>@<i>id</i>@</tt> &mdash; replaced with the user-interface label for the option with id <i>id</i>.
     *            <li><tt>@allcorrect@</tt> &mdash; replaced with a comma-and-"and" separated list of the user-interface labels for all
     *              the correct options.
     *              </ul>
     * @param options
     *            the list of <tt>Option</tt>s to be associated with this question.
     * @param shufflable
     *            whether this question's options can be reordered when presented to the user.
     * @param singleOptionMode
     *            whether this question should always indicate that only one option needs to be selected.
     * @throws IllegalArgumentException
     *             if <tt>singleOptionMode</tt> is true yet there is not one correct <tt>Option</tt> in
     *             <tt>options</tt>.
     */
    public MultipleChoiceQuestion(String questionText, String explanationText, List<Option> options,
            boolean shufflable, boolean singleOptionMode) {
        super(questionText, explanationText);
        Utils.checkArgumentNotNull(options, "options");
        if (options.size() == 0)
            throw new IllegalArgumentException("Must have at least one option in the question");
        this.options = new ArrayList<Option>(options);
        this.shufflable = shufflable;
        this.singleOptionMode = singleOptionMode;

        int numberOfCorrectOptions = numberOfCorrectOptions();
        if (numberOfCorrectOptions == 0)
            throw new IllegalArgumentException("Must specify at least one correct option");

        if (singleOptionMode == true && numberOfCorrectOptions != 1)
            throw new IllegalArgumentException("Single option mode is true but " + numberOfCorrectOptions
                    + " options are correct");
    }

    /**
     * Returns a list of all the options for this question.
     */
    public List<Option> getOptions() {
        return Collections.unmodifiableList(options);
    }

    /**
     * @return the number of correct options
     */
    public int numberOfCorrectOptions() {
        int count = countCorrectOptions(options);
        return count;
    }

    public static int countCorrectOptions(List<Option> localOptions) {
        int count = 0;
        for (Option option : localOptions)
            if (option.isCorrect())
                count++;
        return count;
    }

    /**
     * @return the number of options, both correct and incorrect
     */
    public int numberOfOptions() {
        return options.size();
    }

    /**
     * @return whether this question's options can be reordered when presented to the user.
     */
    public boolean isShufflable() {
        return shufflable;
    }

    /**
     * @return whether this question should always indicate that only one option needs to be selected.
     */
    public boolean isSingleOptionMode() {
        return singleOptionMode;
    }

    public boolean isCorrect(Answer generalAnswer) {
        MultipleChoiceAnswer answer = getMultipleChoiceAnswer(generalAnswer);
        checkOptionsConsistentWithThisQuestion(answer);
        return getCorrectOptions().equals(answer.getOptionsSelected());

    }

    private MultipleChoiceAnswer getMultipleChoiceAnswer(Answer generalAnswer) {
        Utils.checkArgumentNotNull(generalAnswer, "answer");
        MultipleChoiceAnswer answer;
        try {
            answer = (MultipleChoiceAnswer) generalAnswer;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("answer must be a " + MultipleChoiceAnswer.class.getSimpleName()
                    + ", but was passed in a " + generalAnswer.getClass().getSimpleName());
        }
        return answer;
    }

    private Set<Option> getCorrectOptions() {
        Set<Option> correctOptions = new HashSet<Option>();
        for (Option option : options)
            if (option.isCorrect())
                correctOptions.add(option);
        return correctOptions;
    }

    /**
     * Returns whether this answer is judged to constitute a complete answer or not. If the number of options needed has
     * been stated to the user, then their answer is complete only when they have selected that many options. Otherwise,
     * it is judged complete if they have selected any options. This behaviour avoids leaking information about the
     * number of answers to the user. However, this will give the wrong answer if <i>no</i> options are correct (it's
     * expected nearly all questions will have at least one answer correct).
     */
    public boolean isAnswered(Answer generalAnswer, boolean numberOfOptionsNeededIsShown) {
        MultipleChoiceAnswer answer = getMultipleChoiceAnswer(generalAnswer);
        checkOptionsConsistentWithThisQuestion(answer);
        int numberOfOptionsSelected = answer.getOptionsSelected().size();
        if (numberOfOptionsNeededIsShown)
            return numberOfOptionsSelected == numberOfCorrectOptions();
        else
            return numberOfOptionsSelected > 0;
    }

    private void checkOptionsConsistentWithThisQuestion(MultipleChoiceAnswer answer) {
        Set<Option> optionsSelected = answer.getOptionsSelected();
        if (!options.containsAll(optionsSelected))
            throw new IllegalArgumentException("Inapplicable options found in answer: " + answer + ", allowable="
                    + options);
    }

    @Override
    public String getQuestionTypeName() {
        return "Multiple choice";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((options == null) ? 0 : options.hashCode());
        result = prime * result + (shufflable ? 1231 : 1237);
        result = prime * result + (singleOptionMode ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MultipleChoiceQuestion other = (MultipleChoiceQuestion) obj;
        if (options == null) {
            if (other.options != null)
                return false;
        } else if (!options.equals(other.options))
            return false;
        if (shufflable != other.shufflable)
            return false;
        if (singleOptionMode != other.singleOptionMode)
            return false;
        return true;
    }

    public static class Builder {

        private final List<Option> options          = new ArrayList<Option>();
        private boolean            shufflable       = true;
        private boolean            singleOptionMode = false;
        private String             questionText     = null;
        private String             explanationText  = null;

        public MultipleChoiceQuestion build() {
            return new MultipleChoiceQuestion(questionText, explanationText, options, shufflable, singleOptionMode);
        }

        public Builder option(Option option) {
            options.add(option);
            return this;
        }

        public Builder options(List<Option> options_) {
            options.addAll(options_);
            return this;
        }

        public Builder option(String optionText, boolean correct) {
            int nextId = options.size() + 1;
            return option(new Option(optionText, correct, nextId));
        }

        public Builder questionText(String questionText_) {
            this.questionText = questionText_;
            return this;
        }

        public Builder explanationText(String explanationText_) {
            this.explanationText = explanationText_;
            return this;
        }

        public Builder shufflable(boolean shufflable_) {
            this.shufflable = shufflable_;
            return this;
        }

        public Builder shufflable() {
            this.shufflable = true;
            return this;
        }

        public Builder singleOptionMode(boolean singleOptionMode_) {
            this.singleOptionMode = singleOptionMode_;
            return this;
        }

        public Builder singleOptionMode() {
            this.singleOptionMode = true;
            return this;
        }

        public List<Option> getOptions() {
            return options;
        }

        public boolean isShufflable() {
            return shufflable;
        }

        public boolean isSingleOptionMode() {
            return singleOptionMode;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String getExplanationText() {
            return explanationText;
        }
    }

    public MultipleChoiceAnswer initialAnswer() {
        return new MultipleChoiceAnswer(Collections.<Option> emptySet());
    }

}
