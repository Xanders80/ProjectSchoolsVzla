package com.school.academic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.GradingScale;
import com.school.academic.entity.StudyPlan;
import com.school.academic.enums.AcademicLevel;
import com.school.academic.repository.GradingScaleRepository;

@SpringBootTest
@Transactional
public class StudyPlanServiceTest {

    @Autowired
    private StudyPlanService studyPlanService;

    @Autowired
    private GradingScaleRepository gradingScaleRepository;

    @Test
    public void testCreateAndRetrieveStudyPlan() {
        StudyPlan plan = new StudyPlan();
        plan.setName("Plan 2025");
        plan.setAcademicLevel(AcademicLevel.SECONDARY);
        plan.setDescription("Plan test");

        StudyPlan saved = studyPlanService.saveStudyPlan(plan);

        assertThat(saved.getId()).isNotNull();
        assertThat(studyPlanService.getAllStudyPlans()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    public void testGradingScalesAssociation() {
        // Create Plan
        StudyPlan plan = new StudyPlan();
        plan.setName("Plan With Scales");
        plan.setAcademicLevel(AcademicLevel.PRIMARY);
        StudyPlan savedPlan = studyPlanService.saveStudyPlan(plan);

        // Create Scale A
        GradingScale scaleA = new GradingScale();
        scaleA.setLabel("A");
        scaleA.setDescription("Excelente");
        scaleA.setMinScore(new BigDecimal("18.00"));
        scaleA.setMaxScore(new BigDecimal("20.00"));
        scaleA.setStudyPlan(savedPlan);
        gradingScaleRepository.save(scaleA);

        // Fetch scales
        List<GradingScale> scales = gradingScaleRepository.findByStudyPlanId(savedPlan.getId());
        assertThat(scales).hasSize(1);
        assertThat(scales.get(0).getLabel()).isEqualTo("A");
    }
}
