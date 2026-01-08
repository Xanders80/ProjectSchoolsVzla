package com.school.academic.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.school.core.listener.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Registro de evaluación docente anual basada en indicadores.
 */
@Entity
@Table(name = "teacher_evaluations")
@EntityListeners(AuditEntityListener.class)
public class TeacherEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_profile_id", nullable = false)
    @NotNull(message = "El perfil docente es obligatorio")
    private TeacherProfile teacherProfile;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_period_id", nullable = false)
    @NotNull(message = "El período académico es obligatorio")
    private AcademicPeriod academicPeriod;

    @NotNull(message = "La fecha de evaluación es obligatoria")
    private LocalDate evaluationDate = LocalDate.now();

    // Indicadores solicitados
    @Min(0)
    @Max(100)
    @Column(name = "pedagogical_efficiency_score")
    private Double pedagogicalEfficiencyScore;

    @Min(0)
    @Max(100)
    @Column(name = "seniority_score")
    private Double seniorityScore;

    @Min(0)
    @Max(100)
    @Column(name = "academic_formation_score")
    private Double academicFormationScore;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(columnDefinition = "TEXT")
    private String committeeComments;

    @Column(name = "is_finalized", nullable = false)
    private boolean finalized = false;

    @Column(name = "signed_by", length = 150)
    private String signedBy;

    @Column(name = "signature_date")
    private LocalDateTime signatureDate;

    @Column(name = "verification_hash", length = 255)
    private String verificationHash;

    public TeacherEvaluation() {
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

    public AcademicPeriod getAcademicPeriod() {
        return academicPeriod;
    }

    public void setAcademicPeriod(AcademicPeriod academicPeriod) {
        this.academicPeriod = academicPeriod;
    }

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public Double getPedagogicalEfficiencyScore() {
        return pedagogicalEfficiencyScore;
    }

    public void setPedagogicalEfficiencyScore(Double pedagogicalEfficiencyScore) {
        this.pedagogicalEfficiencyScore = pedagogicalEfficiencyScore;
    }

    public Double getSeniorityScore() {
        return seniorityScore;
    }

    public void setSeniorityScore(Double seniorityScore) {
        this.seniorityScore = seniorityScore;
    }

    public Double getAcademicFormationScore() {
        return academicFormationScore;
    }

    public void setAcademicFormationScore(Double academicFormationScore) {
        this.academicFormationScore = academicFormationScore;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public String getCommitteeComments() {
        return committeeComments;
    }

    public void setCommitteeComments(String committeeComments) {
        this.committeeComments = committeeComments;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    public LocalDateTime getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(LocalDateTime signatureDate) {
        this.signatureDate = signatureDate;
    }

    public String getVerificationHash() {
        return verificationHash;
    }

    public void setVerificationHash(String verificationHash) {
        this.verificationHash = verificationHash;
    }

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        this.totalScore = (pedagogicalEfficiencyScore != null ? pedagogicalEfficiencyScore : 0) * 0.5 +
                (seniorityScore != null ? seniorityScore : 0) * 0.2 +
                (academicFormationScore != null ? academicFormationScore : 0) * 0.3;
    }
}
