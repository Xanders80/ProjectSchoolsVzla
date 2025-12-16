package com.school.academic.enums;

public enum EvaluationType {
    EXAM("Examen"),
    HOMEWORK("Tarea"),
    PROJECT("Proyecto"),
    QUIZ("Quiz"),
    PARTICIPATION("Participación");

    private final String displayName;

    EvaluationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
