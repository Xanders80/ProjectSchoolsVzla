package com.school.infra.enums;

/**
 * Estados del ciclo de vida de un activo.
 * Control de estados mutuamente excluyentes para tracking del activo.
 */
public enum AssetStatus {
    ACTIVE("Activo"),
    IN_MAINTENANCE("En Mantenimiento"),
    DAMAGED("Dañado"),
    RETIRED("Retirado"),
    LOST("Extraviado");

    private final String displayName;

    AssetStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determina el estilo CSS de badge según el estado.
     * Para integración con Bootstrap badges en vistas Thymeleaf.
     */
    public String getBadgeClass() {
        switch (this) {
            case ACTIVE:
                return "badge-success";
            case IN_MAINTENANCE:
                return "badge-warning";
            case DAMAGED:
                return "badge-danger";
            case RETIRED:
                return "badge-secondary";
            case LOST:
                return "badge-dark";
            default:
                return "badge-info";
        }
    }
}
