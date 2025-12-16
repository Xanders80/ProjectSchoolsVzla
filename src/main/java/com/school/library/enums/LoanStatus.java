package com.school.library.enums;

public enum LoanStatus {
    ACTIVE("Activo"),
    RETURNED("Devuelto"),
    OVERDUE("Vencido");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
