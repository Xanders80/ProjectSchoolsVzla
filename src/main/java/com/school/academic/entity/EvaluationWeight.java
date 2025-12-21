package com.school.academic.entity;

import com.school.academic.enums.EvaluationType;
import com.school.core.listener.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "evaluation_weights", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "course_id", "evaluation_type" }) })
@EntityListeners(AuditEntityListener.class)
public class EvaluationWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "El curso es obligatorio")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_type", nullable = false)
    @NotNull(message = "El tipo de evaluación es obligatorio")
    private EvaluationType evaluationType;

    @NotNull(message = "El peso es obligatorio")
    @Min(value = 0, message = "El peso mínimo es 0")
    @Max(value = 100, message = "El peso máximo es 100")
    private Double weight; // Percentage (e.g., 20.0 for 20%)

    public EvaluationWeight() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
