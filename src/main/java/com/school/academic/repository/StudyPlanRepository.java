package com.school.academic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.StudyPlan;
import com.school.academic.enums.AcademicLevel;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    Optional<StudyPlan> findByName(String name);

    List<StudyPlan> findByAcademicLevel(AcademicLevel academicLevel);

    List<StudyPlan> findByActiveTrue();
}
