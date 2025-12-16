package com.school.academic.entity;

import com.school.academic.enums.AttendanceStatus;
import com.school.core.listener.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@EntityListeners(AuditEntityListener.class)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "El estudiante es obligatorio")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @NotNull(message = "La sección es obligatoria")
    private Section section;

    @Column(nullable = false)
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private AttendanceStatus status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    public Attendance() {
        this.date = LocalDate.now();
        this.status = AttendanceStatus.PRESENT; // Default
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
