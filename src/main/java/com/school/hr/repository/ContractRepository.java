package com.school.hr.repository;

import com.school.hr.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByStaffId(Long staffId);
}
