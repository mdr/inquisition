package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import uk.co.bytemark.vm.enigma.inquisition.misc.Utils;

/**
 * An answer to a {@link MultipleChoiceQuestion}.
 */
public class MultipleChoiceAnswer implements Answer {

    private final Set<Option> optionsSelected;

    public MultipleChoiceAnswer(Set<Option> optionsSelected) {
        Utils.checkArgumentNotNull(optionsSelected, "optionsSelected");
        for (Option option : optionsSelected)
            if (option == null)
                throw new NullPointerException("option is null in " + optionsSelected);
        this.optionsSelected = new HashSet<Option>(optionsSelected);
    }

    public Set<Option> getOptionsSelected() {
        return Collections.unmodifiableSet(optionsSelected);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + optionsSelected + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((optionsSelected == null) ? 0 : optionsSelected.hashCode());
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
        final MultipleChoiceAnswer other = (MultipleChoiceAnswer) obj;
        if (optionsSelected == null) {
            if (other.optionsSelected != null)
                return false;
        } else if (!optionsSelected.equals(other.optionsSelected))
            return false;
        return true;
    }

}