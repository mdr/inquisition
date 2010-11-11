package uk.co.bytemark.vm.enigma.inquisition.questions;

public enum QuestionType {
    MULTIPLE_CHOICE("Multiple choice", MultipleChoiceQuestion.class), //
    DRAG_AND_DROP("Drag and drop", DragAndDropQuestion.class);

    private final Class<? extends Question> type;
    private final String                    name;

    private QuestionType(String name, Class<? extends Question> type) {
        this.name = name;
        this.type = type;
    }

    public Class<? extends Question> getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
