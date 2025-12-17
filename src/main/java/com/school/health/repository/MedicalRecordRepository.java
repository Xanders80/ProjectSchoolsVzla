package com.school.health.repository;

import com.school.health.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByStudentId(Long studentId);

    @Modifying
    void deleteByStudentId(Long studentId);
}
