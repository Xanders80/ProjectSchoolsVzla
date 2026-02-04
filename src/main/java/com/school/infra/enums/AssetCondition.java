package com.school.infra.enums;

/**
 * Evaluación de la condición física de un activo.
 * Utilizado para decisiones de mantenimiento preventivo vs reemplazo.
 */
public enum AssetCondition {
    EXCELLENT("Excelente", 1.0),
    GOOD("Bueno", 0.85),
    FAIR("Regular", 0.60),
    POOR("Malo", 0.35),
    BROKEN("Roto", 0.10);

    private final String displayName;
    private final double conditionFactor; // Factor para ajuste de valor de activo

    AssetCondition(String displayName, double conditionFactor) {
        this.displayName = displayName;
        this.conditionFactor = conditionFactor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getConditionFactor() {
        return conditionFactor;
    }

    /**
     * Determina si la condición requiere mantenimiento inmediato.
     */
    public boolean requiresMaintenance() {
        return this == POOR || this == BROKEN;
    }

    /**
     * Clase CSS de badge según condición.
     */
    public String getBadgeClass() {
        switch (this) {
            case EXCELLENT:
                return "badge-success";
            case GOOD:
                return "badge-primary";
            case FAIR:
                return "badge-warning";
            case POOR:
                return "badge-danger";
            case BROKEN:
                return "badge-dark";
            default:
                return "badge-secondary";
        }
    }
}
