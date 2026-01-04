package com.school.academic.entity;

import java.math.BigDecimal;

import com.school.core.listener.AuditEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "grading_scales")
@EntityListeners(AuditEntityListener.class)
public class GradingScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "study_plan_id", nullable = false)
    private StudyPlan studyPlan;

    @NotBlank(message = "La letra o etiqueta es obligatoria")
    @Column(length = 5, nullable = false)
    private String label; // A, B, C, AD, etc.

    @NotNull(message = "El rango mínimo es obligatorio")
    @DecimalMin("0.0")
    @Column(name = "min_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal minScore;

    @NotNull(message = "El rango máximo es obligatorio")
    @DecimalMax("100.00") // Asumiendo que pueden usar escalas de 100
    @Column(name = "max_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal maxScore;

    @Column(name = "description")
    private String description; // Excelente, Bueno, Regular...

    public GradingScale() {
    }

    public GradingScale(StudyPlan studyPlan, BigDecimal minScore, BigDecimal maxScore, String label, String description) {
        this.studyPlan = studyPlan;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.label = label;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudyPlan getStudyPlan() {
        return studyPlan;
    }

    public void setStudyPlan(StudyPlan studyPlan) {
        this.studyPlan = studyPlan;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getMinScore() {
        return minScore;
    }

    public void setMinScore(BigDecimal minScore) {
        this.minScore = minScore;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
