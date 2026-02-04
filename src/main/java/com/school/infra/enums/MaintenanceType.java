package com.school.infra.enums;

/**
 * Tipos de mantenimiento según su naturaleza.
 * Utilizado para análisis de costos, planificación y reporting.
 */
public enum MaintenanceType {
    PREVENTIVE("Preventivo", "Mantenimiento programado para prevenir fallas"),
    CORRECTIVE("Correctivo", "Reparación de fallas o daños existentes"),
    EMERGENCY("Emergencia", "Intervención urgente por falla crítica"),
    INSPECTION("Inspección", "Revisión periódica sin intervención");

    private final String displayName;
    private final String description;

    MaintenanceType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Determina la prioridad sugerida según el tipo de mantenimiento.
     */
    public MaintenancePriority getSuggestedPriority() {
        switch (this) {
            case EMERGENCY:
                return MaintenancePriority.CRITICAL;
            case CORRECTIVE:
                return MaintenancePriority.HIGH;
            case PREVENTIVE:
                return MaintenancePriority.MEDIUM;
            case INSPECTION:
                return MaintenancePriority.LOW;
            default:
                return MaintenancePriority.MEDIUM;
        }
    }

    /**
     * Clase CSS de badge según tipo.
     */
    public String getBadgeClass() {
        switch (this) {
            case EMERGENCY:
                return "badge-danger";
            case CORRECTIVE:
                return "badge-warning";
            case PREVENTIVE:
                return "badge-primary";
            case INSPECTION:
                return "badge-info";
            default:
                return "badge-secondary";
        }
    }
}
