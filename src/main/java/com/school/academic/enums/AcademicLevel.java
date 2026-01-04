package com.school.academic.enums;

public enum AcademicLevel {
    PRESCHOOL("Preescolar"),
    PRIMARY("Primaria"),
    SECONDARY("Secundaria"),
    BACHILLERATO("Bachillerato"),
    VOCATIONAL("Técnico"),
    OTHER("Otro");

    private final String description;

    AcademicLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
