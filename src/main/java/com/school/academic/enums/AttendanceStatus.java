package com.school.academic.enums;

public enum AttendanceStatus {
    PRESENT("Presente"),
    ABSENT("Ausente"),
    LATE("Tardanza"),
    EXCUSED("Justificado");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
