package com.school.academic.entity;

import java.time.LocalDate;
import com.school.core.listener.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Representa el desarrollo profesional, capacitaciones y títulos obtenidos por
 * el docente.
 * Integrado con el sistema de Expediente Digital.
 */
@Entity
@Table(name = "teacher_developments")
@EntityListeners(AuditEntityListener.class)
public class TeacherDevelopment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_profile_id", nullable = false)
    @NotNull(message = "El perfil docente es obligatorio")
    private TeacherProfile teacherProfile;

    @NotBlank(message = "El nombre del curso/título es obligatorio")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "La institución es obligatoria")
    @Column(nullable = false, length = 150)
    private String institution;

    @NotNull(message = "La fecha de obtención es obligatoria")
    @Column(name = "attainment_date", nullable = false)
    private LocalDate attainmentDate;

    @Column(name = "folio_number", length = 50)
    private String folioNumber;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "verification_date")
    private LocalDate verificationDate;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "file_path", length = 255)
    private String filePath;

    public TeacherDevelopment() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeacherProfile getTeacherProfile() {
        return teacherProfile;
    }

    public void setTeacherProfile(TeacherProfile teacherProfile) {
        this.teacherProfile = teacherProfile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public LocalDate getAttainmentDate() {
        return attainmentDate;
    }

    public void setAttainmentDate(LocalDate attainmentDate) {
        this.attainmentDate = attainmentDate;
    }

    public String getFolioNumber() {
        return folioNumber;
    }

    public void setFolioNumber(String folioNumber) {
        this.folioNumber = folioNumber;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public LocalDate getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(LocalDate verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
