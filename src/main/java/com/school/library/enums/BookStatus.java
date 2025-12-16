package com.school.library.enums;

public enum BookStatus {
    AVAILABLE("Disponible"),
    BORROWED("Prestado"),
    LOST("Perdido"),
    MAINTENANCE("Mantenimiento");

    private final String displayName;

    BookStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
