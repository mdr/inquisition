package uk.co.bytemark.vm.enigma.inquisition.gui.editor;

import uk.co.bytemark.vm.enigma.inquisition.questions.Question;

public interface IQuestionEditorPanel {

    Question getQuestion();

    void setQuestionValidityListener(QuestionValidityListener listener);

    public interface QuestionValidityListener {
        public void questionValid(boolean valid);
    }

}
