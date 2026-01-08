package com.school.infra.enums;

public enum ReservationStatus {
    PENDING("Pendiente"),
    APPROVED("Aprobada"),
    REJECTED("Rechazada"),
    CANCELLED("Cancelada"),
    COMPLETED("Completada");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
