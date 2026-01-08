package com.school.academic.dto;

import com.school.academic.entity.Student;
import com.school.academic.enums.AttendanceStatus;

public class AttendanceDTO {
    private Student student;
    private AttendanceStatus status;
    private Long attendanceId;
    private String remarks;

    public AttendanceDTO(Student student, AttendanceStatus status, Long attendanceId, String remarks) {
        this.student = student;
        this.status = status;
        this.attendanceId = attendanceId;
        this.remarks = remarks;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
