package com.school.hr.repository;

import com.school.hr.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByPeriod(String period);

    List<Payroll> findByStaffId(Long staffId);

    boolean existsByStaffIdAndPeriod(Long staffId, String period);
}
