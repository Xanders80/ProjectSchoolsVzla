package com.school.health.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.school.health.entity.Vaccine;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    List<Vaccine> findByStudentIdOrderByAdministrationDateDesc(Long studentId);

    @Modifying
    void deleteByStudentId(Long studentId);
}
