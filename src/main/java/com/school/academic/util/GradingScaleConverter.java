package com.school.academic.util;

public class GradingScaleConverter {

    public static String toLetter(double score) {
        if (score >= 18)
            return "A";
        if (score >= 15)
            return "B";
        if (score >= 12)
            return "C";
        if (score >= 10)
            return "D";
        return "F";
    }

    public static String getDescription(String letter) {
        return switch (letter) {
            case "A" -> "Excelente";
            case "B" -> "Muy Bueno";
            case "C" -> "Bueno";
            case "D" -> "Regular";
            case "F" -> "Reprobado";
            default -> "N/A";
        };
    }
}
