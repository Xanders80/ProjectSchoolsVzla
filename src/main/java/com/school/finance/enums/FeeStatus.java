package com.school.finance.enums;

public enum FeeStatus {
    PENDING("Pendiente"),
    PARTIAL("Parcial"),
    PAID("Pagado"),
    OVERDUE("Vencido");

    private final String displayName;

    FeeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
