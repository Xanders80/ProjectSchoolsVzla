package com.school.hr.entity;

import com.school.admin.entity.Staff;
import com.school.core.listener.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "staff_payroll")
@EntityListeners(AuditEntityListener.class)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payroll_staff"))
    private Staff staff;

    @NotNull
    @Column(nullable = false, length = 7) // Format YYYY-MM
    private String period;

    @Column(name = "base_salary", precision = 10, scale = 2)
    private BigDecimal baseSalary;

    @Column(precision = 10, scale = 2)
    private BigDecimal bonuses = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "net_salary", precision = 10, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    public enum PaymentStatus {
        PENDING, PAID, CANCELLED
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getBonuses() {
        return bonuses;
    }

    public void setBonuses(BigDecimal bonuses) {
        this.bonuses = bonuses;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
