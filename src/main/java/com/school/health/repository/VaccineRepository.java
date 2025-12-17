package com.school.health.repository;

import com.school.health.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    List<Vaccine> findByStudentIdOrderByAdministrationDateDesc(Long studentId);

    @Modifying
    void deleteByStudentId(Long studentId);
}
