package com.school.academic.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.AdmissionExam;

@Repository
public interface AdmissionExamRepository extends JpaRepository<AdmissionExam, Long> {
    Optional<AdmissionExam> findByApplicantDni(String applicantDni);

    List<AdmissionExam> findByExamDate(LocalDate date);

    List<AdmissionExam> findByStatus(String status);
}
