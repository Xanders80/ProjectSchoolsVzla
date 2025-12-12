package com.school.admin.entity;

import com.school.core.entity.Person;
import com.school.core.entity.User;
import com.school.core.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "staff")
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Staff extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @jakarta.validation.constraints.NotNull(message = "El cargo es obligatorio")
    @Enumerated(EnumType.STRING)
    private Role jobTitle; // TEACHER, DIRECTOR, STAFF

    @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = false, message = "El salario debe ser mayor a 0")
    private BigDecimal salary;

    @jakarta.validation.constraints.NotNull(message = "La fecha de contratación es obligatoria")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    @jakarta.validation.constraints.NotBlank(message = "El departamento es obligatorio")
    private String department;

    // For Teachers specifically
    private String specialization;

    public Staff() {
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

    public Role getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(Role jobTitle) {
        this.jobTitle = jobTitle;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
