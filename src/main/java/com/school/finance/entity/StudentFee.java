package com.school.finance.entity;

import com.school.academic.entity.Student;
import com.school.core.listener.AuditEntityListener;
import com.school.finance.enums.FeeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "student_fees")
@EntityListeners(AuditEntityListener.class)
public class StudentFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "El estudiante es obligatorio")
    private Student student;

    @Column(nullable = false)
    @NotBlank(message = "La descripción es obligatoria")
    private String description; // e.g., "Matrícula 2025", "Mensualidad Marzo"

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private FeeStatus status;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    public StudentFee() {
        this.status = FeeStatus.PENDING;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public FeeStatus getStatus() {
        return status;
    }

    public void setStatus(FeeStatus status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
