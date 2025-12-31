package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.GradingScale;

@Repository
public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {

    List<GradingScale> findByStudyPlanId(Long studyPlanId);

    @Query("SELECT gs FROM GradingScale gs WHERE gs.studyPlan.id = :studyPlanId AND :score BETWEEN gs.minScore AND gs.maxScore")
    GradingScale findByStudyPlanAndScore(@Param("studyPlanId") Long studyPlanId,
            @Param("score") java.math.BigDecimal score);
}
