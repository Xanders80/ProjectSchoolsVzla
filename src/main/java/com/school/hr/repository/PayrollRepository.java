package com.school.hr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.hr.entity.Payroll;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByPeriod(String period);

    List<Payroll> findByStaffId(Long staffId);

    boolean existsByStaffIdAndPeriod(Long staffId, String period);
}
