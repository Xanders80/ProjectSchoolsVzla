package com.school.admin.entity;

import com.school.core.entity.Person;
import com.school.core.entity.User;
import com.school.core.enums.Role;
import com.school.academic.validation.ValidationGroups;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "staff",
       indexes = {
           @Index(name = "idx_staff_job_title", columnList = "jobTitle"),
           @Index(name = "idx_staff_department", columnList = "department"),
           @Index(name = "idx_staff_hire_date", columnList = "hireDate")
       })
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Staff extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_staff_user"))
    private User user;

    @NotNull(message = "El cargo es obligatorio", groups = ValidationGroups.Create.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "job_title", nullable = false, length = 20)
    private Role jobTitle;

    @DecimalMin(value = "0.0", inclusive = false, message = "El salario debe ser mayor a 0", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @NotNull(message = "La fecha de contratación es obligatoria", groups = ValidationGroups.Create.class)
    @PastOrPresent(message = "La fecha de contratación no puede ser futura", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @NotBlank(message = "El departamento es obligatorio", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 2, max = 100, message = "El departamento debe tener entre 2 y 100 caracteres", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "previous_department", length = 100)
    private String previousDepartment;

    @Column(name = "department_changed_at")
    private LocalDateTime departmentChangedAt;

    @Size(max = 200, message = "La especialización no puede exceder los 200 caracteres", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Column(name = "specialization", length = 200)
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
        String newValue = department != null ? department.trim() : null;
        if (this.department != null && !Objects.equals(this.department, newValue)) {
            this.previousDepartment = this.department;
            this.departmentChangedAt = LocalDateTime.now();
        }
        this.department = newValue;
    }

    public String getPreviousDepartment() {
        return previousDepartment;
    }

    public LocalDateTime getDepartmentChangedAt() {
        return departmentChangedAt;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization != null ? specialization.trim() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Staff staff)) return false;
        return Objects.equals(id, staff.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Staff{id=" + id + ", jobTitle=" + jobTitle + ", department='" + department + "'}";
    }
}
