package com.school.health.entity;

import com.school.academic.entity.Student;
import com.school.core.listener.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "vaccines")
@EntityListeners(AuditEntityListener.class)
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vaccine_student"))
    private Student student;

    @NotBlank(message = "El nombre de la vacuna es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "dose_number")
    private Integer doseNumber;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha inválida")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "administration_date", nullable = false)
    private LocalDate administrationDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "expiration_date")
    private LocalDate expirationDate;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(Integer doseNumber) {
        this.doseNumber = doseNumber;
    }

    public LocalDate getAdministrationDate() {
        return administrationDate;
    }

    public void setAdministrationDate(LocalDate administrationDate) {
        this.administrationDate = administrationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
