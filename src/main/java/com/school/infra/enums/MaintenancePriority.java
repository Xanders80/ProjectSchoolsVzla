package com.school.infra.enums;

/**
 * Niveles de prioridad para solicitudes de mantenimiento.
 * Cada prioridad tiene un SLA (Service Level Agreement) en días para
 * resolución.
 */
public enum MaintenancePriority {
    LOW("Baja", 30, 1),
    MEDIUM("Media", 15, 2),
    HIGH("Alta", 7, 3),
    CRITICAL("Crítica", 1, 4);

    private final String displayName;
    private final int slaDays;
    private final int urgencyLevel; // 1-4, para ordenamiento

    MaintenancePriority(String displayName, int slaDays, int urgencyLevel) {
        this.displayName = displayName;
        this.slaDays = slaDays;
        this.urgencyLevel = urgencyLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene el número de días de SLA para esta prioridad.
     */
    public int getSlaDays() {
        return slaDays;
    }

    public int getUrgencyLevel() {
        return urgencyLevel;
    }

    /**
     * Clase CSS de badge según prioridad.
     */
    public String getBadgeClass() {
        switch (this) {
            case CRITICAL:
                return "badge-danger";
            case HIGH:
                return "badge-warning";
            case MEDIUM:
                return "badge-info";
            case LOW:
                return "badge-secondary";
            default:
                return "badge-light";
        }
    }

    /**
     * Icono visual para la prioridad.
     */
    public String getIcon() {
        switch (this) {
            case CRITICAL:
                return "fas fa-exclamation-triangle";
            case HIGH:
                return "fas fa-arrow-up";
            case MEDIUM:
                return "fas fa-minus";
            case LOW:
                return "fas fa-arrow-down";
            default:
                return "fas fa-info-circle";
        }
    }
}
