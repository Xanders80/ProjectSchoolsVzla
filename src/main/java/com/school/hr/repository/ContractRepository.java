package com.school.hr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.hr.entity.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByStaffId(Long staffId);
}
