package com.school.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.finance.entity.StudentFee;
import com.school.finance.enums.FeeStatus;

public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {
    List<StudentFee> findByStudentId(Long studentId);

    List<StudentFee> findByStatus(FeeStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT f.status, COUNT(f) FROM StudentFee f GROUP BY f.status")
    List<Object[]> countFeesByStatus();

    // Sum amount where status = ?
    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.amount) FROM StudentFee f WHERE f.status = :status")
    java.math.BigDecimal sumAmountByStatus(FeeStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT YEAR(f.paymentDate), MONTH(f.paymentDate), SUM(f.amount) FROM StudentFee f WHERE f.status = 'PAID' AND f.paymentDate IS NOT NULL GROUP BY YEAR(f.paymentDate), MONTH(f.paymentDate) ORDER BY YEAR(f.paymentDate) DESC, MONTH(f.paymentDate) DESC")
    List<Object[]> sumPaidFeesByMonth();
}
