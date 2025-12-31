package com.school.academic.entity;

import com.school.admin.entity.Staff;
import com.school.core.listener.AuditEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "teacher_profiles")
@EntityListeners(AuditEntityListener.class)
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false, unique = true)
    @NotNull(message = "El registro de personal (Staff) es obligatorio")
    private Staff staff;

    @Column(name = "academic_title", length = 150)
    @Size(max = 150, message = "El título académico no puede exceder 150 caracteres")
    private String academicTitle;

    @Column(name = "specialization_area", length = 100)
    @Size(max = 100, message = "El área de especialización no puede exceder 100 caracteres")
    private String specializationArea;

    @Column(name = "max_hours_per_week")
    private Integer maxHoursPerWeek;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    public TeacherProfile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getAcademicTitle() {
        return academicTitle;
    }

    public void setAcademicTitle(String academicTitle) {
        this.academicTitle = academicTitle;
    }

    public String getSpecializationArea() {
        return specializationArea;
    }

    public void setSpecializationArea(String specializationArea) {
        this.specializationArea = specializationArea;
    }

    public Integer getMaxHoursPerWeek() {
        return maxHoursPerWeek;
    }

    public void setMaxHoursPerWeek(Integer maxHoursPerWeek) {
        this.maxHoursPerWeek = maxHoursPerWeek;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
