/*
 * QuestionPanel.java
 * 
 * Created on 03 September 2006, 08:16
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import uk.co.bytemark.vm.enigma.inquisition.questions.Answer;
import uk.co.bytemark.vm.enigma.inquisition.questions.Question;

/**
 * 
 * @author Matt
 */
public abstract class QuestionPanel extends javax.swing.JPanel {

    public abstract Answer getAnswer();

    public abstract void setAnswer(Answer answer);

    public abstract Question getQuestion();

    abstract void enterReviewMode();

    abstract void enterQuestionMode();

    public String getExplanationText() {
        return getQuestion().getExplanationText();
    }

}
