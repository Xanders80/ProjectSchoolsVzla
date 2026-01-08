package com.school.academic.enums;

public enum EnrollmentStatus {
    ACTIVE("Activo"),
    COMPLETED("Completado"),
    WITHDRAWN("Retirado"),
    PENDING("Pendiente"),
    SUSPENDED("Suspendido");

    private final String displayName;

    EnrollmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
