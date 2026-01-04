package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.school.academic.entity.CurriculumGrid;

public interface CurriculumGridRepository extends JpaRepository<CurriculumGrid, Long> {
    
    List<CurriculumGrid> findByStudyPlanId(Long studyPlanId);
    
    List<CurriculumGrid> findByGradeLevel(Integer gradeLevel);
    
    @Query("SELECT cg FROM CurriculumGrid cg WHERE cg.studyPlan.id = ?1 AND cg.gradeLevel = ?2")
    CurriculumGrid findByStudyPlanAndGradeLevel(Long studyPlanId, Integer gradeLevel);
    
    long countByStudyPlanId(Long studyPlanId);
}