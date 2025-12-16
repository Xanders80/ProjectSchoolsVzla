package com.school.library.repository;

import com.school.library.entity.Loan;
import com.school.library.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByBorrowerId(Long userId);

    List<Loan> findByStatus(LoanStatus status);
}
