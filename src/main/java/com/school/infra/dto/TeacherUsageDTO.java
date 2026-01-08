package com.school.infra.dto;

public class TeacherUsageDTO {
    private String teacherName;
    private String teacherDni;
    private Long reservationCount;
    private Long totalHours;
    private Long approvedCount;

    public TeacherUsageDTO() {
    }

    public TeacherUsageDTO(String teacherName, String teacherDni, Long reservationCount, Long totalHours,
            Long approvedCount) {
        this.teacherName = teacherName;
        this.teacherDni = teacherDni;
        this.reservationCount = reservationCount;
        this.totalHours = totalHours;
        this.approvedCount = approvedCount;
    }

    // Getters and Setters
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherDni() {
        return teacherDni;
    }

    public void setTeacherDni(String teacherDni) {
        this.teacherDni = teacherDni;
    }

    public Long getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(Long reservationCount) {
        this.reservationCount = reservationCount;
    }

    public Long getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Long totalHours) {
        this.totalHours = totalHours;
    }

    public Long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(Long approvedCount) {
        this.approvedCount = approvedCount;
    }
}
