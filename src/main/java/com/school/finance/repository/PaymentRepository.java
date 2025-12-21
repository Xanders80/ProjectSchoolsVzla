package com.school.finance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.finance.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudentFeeId(Long feeId);
}
