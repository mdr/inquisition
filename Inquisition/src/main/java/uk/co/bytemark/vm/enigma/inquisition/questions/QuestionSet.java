/*
 * QuestionSet.java
 * 
 * Created on 04 September 2006, 17:17
 */

package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * 
 * A set of {@link Question}s bundle together with information about the set.
 */
public class QuestionSet implements Iterable<Question>, Serializable {

    private final String         name;

    private final String         description;

    private final int            recommendedTimePerQuestion; // in seconds

    private final String         category;

    private final List<Question> questions;

    /**
     * Creates a new instance of <tt>QuestionSet</tt>
     * 
     * @param name
     *            the name or title of this set of questions.
     * @param description
     *            description and other information, in HTML text.
     * @param recommendedTimePerQuestion
     *            suggested time, in seconds, needed to answer one question.
     * @param category
     *            a colon (:) delimited string showing where in the category hierarchy the question set lies.
     * @param questions
     *            list of <tt>Question</tt>s.
     */
    public QuestionSet(String name, String description, int recommendedTimePerQuestion, String category,
            List<Question> questions) {
        Utils.checkArgumentNotNull(name, "name");
        Utils.checkArgumentNotNull(description, "description");
        Utils.checkPositive(recommendedTimePerQuestion, "recommendedTimePerQuestion");
        Utils.checkArgumentNotNull(category, "category");
        Utils.checkArgumentNotNull(questions, "questions");
        Preconditions.checkContentsNotNull(questions, "No question can be null");

        this.name = name;
        this.description = description;
        this.recommendedTimePerQuestion = recommendedTimePerQuestion;
        this.category = category;
        this.questions = ImmutableList.copyOf(questions);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public int getRecommendedTimePerQuestion() {
        return recommendedTimePerQuestion;

    }

    public int getRecommendedTimeForAllQuestions() {
        return recommendedTimePerQuestion * numberOfQuestions();
    }

    public int numberOfQuestions() {
        return questions.size();
    }

    public int size() {
        return numberOfQuestions();
    }

    public List<String> getCategoryList() {
        if (category == null || category.equals(""))
            return Collections.emptyList();
        else
            return Arrays.asList(category.split(":"));
    }

    public String getCategorySequence() {
        return category;
    }

    @Override
    public String toString() {
        return QuestionSet.class.getSimpleName() + "(\"" + getName() + "\")";
    }

    /**
     * Returns the number of instances of a class type in the question set
     * 
     * @param c
     *            The class type in question
     */
    public int numberOfType(Class<? extends Question> c) {
        int count = 0;
        for (Question question : questions)
            if (c.isInstance(question))
                count++;
        return count;
    }

    public Iterator<Question> iterator() {
        return Collections.unmodifiableList(questions).iterator();
    }

    public static class QuestionSetBuilder {

        private String               name                       = null;
        private String               description                = "";
        private int                  recommendedTimePerQuestion = 30;
        private String               category                   = "";
        private final List<Question> questions                  = new ArrayList<Question>();

        public QuestionSet build() {
            if (name == null)
                throw new IllegalStateException("Name required");
            return new QuestionSet(name, description, recommendedTimePerQuestion, category, questions);
        }

        public QuestionSetBuilder name(String name_) {
            this.name = name_;
            return this;
        }

        public QuestionSetBuilder description(String description_) {
            this.description = description_;
            return this;
        }

        public QuestionSetBuilder recommendedTime(int recommendedTime_) {
            this.recommendedTimePerQuestion = recommendedTime_;
            return this;
        }

        public QuestionSetBuilder category(String category_) {
            this.category = category_;
            return this;
        }

        public QuestionSetBuilder addQuestions(Question... questions_) {
            for (Question question : questions_)
                this.questions.add(question);
            return this;
        }

        public QuestionSetBuilder addQuestion(Question question) {
            this.questions.add(question);
            return this;
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((questions == null) ? 0 : questions.hashCode());
        result = prime * result + recommendedTimePerQuestion;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final QuestionSet other = (QuestionSet) obj;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (questions == null) {
            if (other.questions != null)
                return false;
        } else if (!questions.equals(other.questions))
            return false;
        if (recommendedTimePerQuestion != other.recommendedTimePerQuestion)
            return false;
        return true;
    }

}
