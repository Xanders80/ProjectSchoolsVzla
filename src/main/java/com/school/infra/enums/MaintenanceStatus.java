package com.school.infra.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estados del workflow de solicitudes de mantenimiento.
 * Incluye validación de transiciones permitidas según máquina de estados.
 *
 * Flujo válido:
 * PENDING → IN_PROGRESS → COMPLETED
 * ↘ ↘ CANCELLED
 */
public enum MaintenanceStatus {
    PENDING("Pendiente"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completado"),
    CANCELLED("Cancelado");

    private final String displayName;

    MaintenanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Valida si es posible transicionar desde el estado actual al estado objetivo.
     *
     * @param target Estado objetivo
     * @return true si la transición es permitida
     */
    public boolean canTransitionTo(MaintenanceStatus target) {
        if (this == target) {
            return false; // No tiene sentido transicionar al mismo estado
        }

        switch (this) {
            case PENDING:
                // Desde Pendiente puede ir a En Progreso o Cancelado
                return target == IN_PROGRESS || target == CANCELLED;

            case IN_PROGRESS:
                // Desde En Progreso puede ir a Completado o Cancelado
                return target == COMPLETED || target == CANCELLED;

            case COMPLETED:
            case CANCELLED:
                // Estados finales - no se pueden reabrir
                return false;

            default:
                return false;
        }
    }

    /**
     * Obtiene los estados a los que se puede transicionar desde el estado actual.
     */
    public Set<MaintenanceStatus> getAllowedTransitions() {
        Set<MaintenanceStatus> allowed = EnumSet.noneOf(MaintenanceStatus.class);
        for (MaintenanceStatus status : MaintenanceStatus.values()) {
            if (this.canTransitionTo(status)) {
                allowed.add(status);
            }
        }
        return allowed;
    }

    /**
     * Indica si el estado es final (no permite más transiciones).
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == CANCELLED;
    }

    /**
     * Clase CSS de badge según estado.
     */
    public String getBadgeClass() {
        switch (this) {
            case PENDING:
                return "badge-warning";
            case IN_PROGRESS:
                return "badge-info";
            case COMPLETED:
                return "badge-success";
            case CANCELLED:
                return "badge-secondary";
            default:
                return "badge-light";
        }
    }
}
