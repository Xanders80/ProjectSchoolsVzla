package com.school.hr.entity;

import java.time.LocalDateTime;
import com.school.academic.entity.TeacherProfile;
import com.school.core.listener.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Registro del régimen disciplinario para docentes.
 * Garantiza el debido proceso y trazabilidad de faltas y sanciones.
 */
@Entity
@Table(name = "disciplinary_records")
@EntityListeners(AuditEntityListener.class)
public class DisciplinaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_profile_id", nullable = false)
    @NotNull(message = "El perfil docente es obligatorio")
    private TeacherProfile teacherProfile;

    @NotBlank(message = "El tipo de falta es obligatorio")
    @Column(name = "infraction_type", nullable = false, length = 100)
    private String infractionType;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "La fecha del incidente es obligatoria")
    @Column(name = "incident_date", nullable = false)
    private LocalDateTime incidentDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "INICIADO"; // INICIADO, NOTIFICADO, DESCARGOS, RESUELTO, ARCHIVADO

    @Column(name = "sanction", length = 200)
    private String sanction;

    @Column(name = "resolution_date")
    private LocalDateTime resolutionDate;

    @Column(columnDefinition = "TEXT")
    private String committeeResolution;

    public DisciplinaryRecord() {
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

    public String getInfractionType() {
        return infractionType;
    }

    public void setInfractionType(String infractionType) {
        this.infractionType = infractionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDateTime incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSanction() {
        return sanction;
    }

    public void setSanction(String sanction) {
        this.sanction = sanction;
    }

    public LocalDateTime getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDateTime resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getCommitteeResolution() {
        return committeeResolution;
    }

    public void setCommitteeResolution(String committeeResolution) {
        this.committeeResolution = committeeResolution;
    }
}
