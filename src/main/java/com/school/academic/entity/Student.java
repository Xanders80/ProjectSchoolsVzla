package com.school.academic.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import com.school.academic.validation.ValidationGroups;
import com.school.core.entity.Person;
import com.school.core.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "students", 
       indexes = {
           @Index(name = "idx_student_registration", columnList = "registrationNumber", unique = true),
           @Index(name = "idx_student_enrollment_date", columnList = "enrollmentDate")
       })
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Student extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_student_user"))
    private User user;

    @NotBlank(message = "El número de registro es obligatorio", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 3, max = 20, message = "El número de registro debe tener entre 3 y 20 caracteres", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "El número de registro solo puede contener letras mayúsculas, números y guiones", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "registration_number", nullable = false, unique = true, length = 20)
    private String registrationNumber;

    @Column(name = "previous_registration_number", length = 20)
    private String previousRegistrationNumber;

    @Column(name = "registration_changed_at")
    private LocalDateTime registrationChangedAt;

    @NotNull(message = "La fecha de inscripción es obligatoria", groups = ValidationGroups.Create.class)
    @PastOrPresent(message = "La fecha de inscripción no puede ser futura", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Student() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        String newValue = registrationNumber != null ? registrationNumber.trim().toUpperCase() : null;
        if (this.registrationNumber != null && !Objects.equals(this.registrationNumber, newValue)) {
            this.previousRegistrationNumber = this.registrationNumber;
            this.registrationChangedAt = LocalDateTime.now();
        }
        this.registrationNumber = newValue;
    }

    public String getPreviousRegistrationNumber() {
        return previousRegistrationNumber;
    }

    public LocalDateTime getRegistrationChangedAt() {
        return registrationChangedAt;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        if (deleted && this.deletedAt == null) {
            this.deletedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return Objects.equals(id, student.id) && 
               Objects.equals(registrationNumber, student.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registrationNumber);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", registrationNumber='" + registrationNumber + "'}";
    }
}
