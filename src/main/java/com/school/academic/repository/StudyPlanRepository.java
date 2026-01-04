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
    
    Optional<StudyPlan> findByIdAndActiveTrue(Long id);
    
    org.springframework.data.domain.Page<StudyPlan> findByActiveTrue(org.springframework.data.domain.Pageable pageable);

    List<StudyPlan> findByAcademicLevel(AcademicLevel academicLevel);

    List<StudyPlan> findByActiveTrue();
    
    List<StudyPlan> findByAcademicLevelAndActiveTrue(AcademicLevel academicLevel);
    
    boolean existsByNameAndActiveTrue(String name);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(sp) FROM StudyPlan sp WHERE sp.active = true")
    long countActiveStudyPlans();
    
    @org.springframework.data.jpa.repository.Query("SELECT sp FROM StudyPlan sp WHERE sp.active = true AND sp.name LIKE %?1%")
    List<StudyPlan> searchByNameContaining(String name);
}
