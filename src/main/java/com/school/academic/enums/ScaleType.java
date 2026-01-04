package com.school.academic.enums;

public enum ScaleType {
    NUMERIC_20("Numérica 0-20"),
    NUMERIC_100("Numérica 0-100"), 
    LETTERS("Letras A-F");

    private final String description;

    ScaleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}