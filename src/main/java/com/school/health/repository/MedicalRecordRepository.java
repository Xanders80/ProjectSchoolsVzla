package com.school.health.repository;

import com.school.health.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByStudentId(Long studentId);
}
