package com.school.academic.entity;

import com.school.core.entity.Person;
import com.school.core.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Student extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @jakarta.validation.constraints.NotBlank(message = "El número de registro es obligatorio")
    @jakarta.validation.constraints.Size(min = 3, max = 20, message = "El número de registro debe tener entre 3 y 20 caracteres")
    private String registrationNumber; // Unique Student ID

    @jakarta.validation.constraints.NotNull(message = "La fecha de inscripción es obligatoria")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate;

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
        this.registrationNumber = registrationNumber;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}
