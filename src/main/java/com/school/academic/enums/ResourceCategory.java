package com.school.academic.enums;

public enum ResourceCategory {
    SYLLABUS("Syllabus/Guía Docente"),
    BIBLIOGRAPHY("Bibliografía"),
    PRESENTATION("Presentación/Clase"),
    EXERCISE("Ejercicios/Práctica"),
    EVALUATION("Evaluación"),
    VIDEO("Video Educativo"),
    INTERACTIVE("Recurso Interactivo"),
    LAB_GUIDE("Guía de Laboratorio"),
    OTHER("Otros");

    private final String description;

    ResourceCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
