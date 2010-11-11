/*
 * DragAndDropAnswer.java
 * 
 * Created on 11 September 2006, 12:44
 */
package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An answer to a {@link DragAndDropQuestion}.
 */
public class DragAndDropAnswer implements Answer {

    private final List<String> slotAnswers;

    public DragAndDropAnswer(List<String> slotAnswers) {
        this.slotAnswers = new ArrayList<String>(slotAnswers);
    }

    public List<String> getSlotAnswers() {
        return Collections.unmodifiableList(slotAnswers);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + slotAnswers + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((slotAnswers == null) ? 0 : slotAnswers.hashCode());
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
        final DragAndDropAnswer other = (DragAndDropAnswer) obj;
        if (slotAnswers == null) {
            if (other.slotAnswers != null)
                return false;
        } else if (!slotAnswers.equals(other.slotAnswers))
            return false;
        return true;
    }

}
