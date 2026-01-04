package com.school.academic.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.school.core.listener.AuditEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "admission_exams")
@EntityListeners(AuditEntityListener.class)
public class AdmissionExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El DNI del postulante es obligatorio")
    @Size(max = 20)
    @Column(name = "applicant_dni", nullable = false, length = 20)
    private String applicantDni;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150)
    @Column(name = "applicant_name", nullable = false, length = 150)
    private String applicantName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(name = "applicant_email", nullable = false, length = 100)
    private String applicantEmail;

    @NotNull(message = "La fecha del examen es obligatoria")
    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Min(0)
    @Max(100) // Assuming 100/100 scale or similar
    @Column(name = "score")
    private Double score;

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AdmissionExam() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicantDni() {
        return applicantDni;
    }

    public void setApplicantDni(String applicantDni) {
        this.applicantDni = applicantDni;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
