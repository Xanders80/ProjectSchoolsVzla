package com.school.academic.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "curriculum_grids")
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class CurriculumGrid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "study_plan_id", nullable = false)
    private StudyPlan studyPlan;

    @NotNull(message = "El nivel de grado es obligatorio")
    @Min(value = 1, message = "El nivel debe ser mayor a 0")
    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    @ManyToMany
    @JoinTable(
        name = "curriculum_required_courses",
        joinColumns = @JoinColumn(name = "curriculum_grid_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> requiredCourses;

    @ManyToMany
    @JoinTable(
        name = "curriculum_elective_courses", 
        joinColumns = @JoinColumn(name = "curriculum_grid_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> electiveCourses;

    @Column(name = "total_hours")
    private Integer totalHours;

    @Column(name = "min_electives")
    private Integer minElectives = 0;

    public CurriculumGrid() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudyPlan getStudyPlan() { return studyPlan; }
    public void setStudyPlan(StudyPlan studyPlan) { this.studyPlan = studyPlan; }

    public Integer getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(Integer gradeLevel) { this.gradeLevel = gradeLevel; }

    public List<Course> getRequiredCourses() { return requiredCourses; }
    public void setRequiredCourses(List<Course> requiredCourses) { this.requiredCourses = requiredCourses; }

    public List<Course> getElectiveCourses() { return electiveCourses; }
    public void setElectiveCourses(List<Course> electiveCourses) { this.electiveCourses = electiveCourses; }

    public Integer getTotalHours() { return totalHours; }
    public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }

    public Integer getMinElectives() { return minElectives; }
    public void setMinElectives(Integer minElectives) { this.minElectives = minElectives; }
}