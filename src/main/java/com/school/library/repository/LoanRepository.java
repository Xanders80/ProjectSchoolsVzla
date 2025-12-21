package com.school.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.library.entity.Loan;
import com.school.library.enums.LoanStatus;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByBorrowerId(Long userId);

    List<Loan> findByStatus(LoanStatus status);
}
