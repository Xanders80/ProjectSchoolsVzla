package com.school.academic.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "teacher_assignments")
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_profile_id", nullable = false)
    @NotNull(message = "El perfil docente es obligatorio")
    private TeacherProfile teacherProfile;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "El curso es obligatorio")
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_period_id", nullable = false)
    @NotNull(message = "El período académico es obligatorio")
    private AcademicPeriod academicPeriod;

    @Column(name = "assigned_hours")
    private Integer assignedHours;

    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate = LocalDate.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "compatibility_score")
    private Double compatibilityScore;

    public TeacherAssignment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TeacherProfile getTeacherProfile() { return teacherProfile; }
    public void setTeacherProfile(TeacherProfile teacherProfile) { this.teacherProfile = teacherProfile; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public AcademicPeriod getAcademicPeriod() { return academicPeriod; }
    public void setAcademicPeriod(AcademicPeriod academicPeriod) { this.academicPeriod = academicPeriod; }

    public Integer getAssignedHours() { return assignedHours; }
    public void setAssignedHours(Integer assignedHours) { this.assignedHours = assignedHours; }

    public LocalDate getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(LocalDate assignmentDate) { this.assignmentDate = assignmentDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Double getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(Double compatibilityScore) { this.compatibilityScore = compatibilityScore; }
}