package com.school.academic.entity;

import com.school.academic.enums.EvaluationType;
import com.school.core.listener.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "grades")
@EntityListeners(AuditEntityListener.class)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "El estudiante es obligatorio")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "El curso es obligatorio")
    private Course course;

    @Column(nullable = false)
    @NotBlank(message = "El periodo es obligatorio")
    private String term; // e.g. "2025-1"

    @Column(nullable = false)
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 0, message = "La calificación mínima es 0")
    @Max(value = 100, message = "La calificación máxima es 100")
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El tipo de evaluación es obligatorio")
    private EvaluationType evaluationType;

    @Column(nullable = false)
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String comments;

    public Grade() {
        this.date = LocalDate.now();
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
