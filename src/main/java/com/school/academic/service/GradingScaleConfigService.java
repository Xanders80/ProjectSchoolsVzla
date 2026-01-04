package com.school.academic.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.GradingScale;
import com.school.academic.entity.StudyPlan;
import com.school.academic.enums.ScaleType;
import com.school.academic.repository.GradingScaleRepository;

@Service
@Transactional
public class GradingScaleConfigService {

    private final GradingScaleRepository gradingScaleRepository;

    public GradingScaleConfigService(GradingScaleRepository gradingScaleRepository) {
        this.gradingScaleRepository = gradingScaleRepository;
    }

    public void setupScale(StudyPlan studyPlan, ScaleType scaleType) {
        // Eliminar escalas existentes para este plan
        gradingScaleRepository.deleteByStudyPlan(studyPlan);

        switch (scaleType) {
            case NUMERIC_20 -> createNumericScale(studyPlan, 0, 20);
            case NUMERIC_100 -> createNumericScale(studyPlan, 0, 100);
            case LETTERS -> createLetterScale(studyPlan);
        }
    }

    private void createNumericScale(StudyPlan studyPlan, int min, int max) {
        List<GradingScale> scales = Arrays.asList(
                new GradingScale(studyPlan, BigDecimal.valueOf(max * 0.9), BigDecimal.valueOf(max), "A", "Excelente"),
                new GradingScale(studyPlan, BigDecimal.valueOf(max * 0.8), BigDecimal.valueOf(max * 0.89), "B",
                        "Bueno"),
                new GradingScale(studyPlan, BigDecimal.valueOf(max * 0.7), BigDecimal.valueOf(max * 0.79), "C",
                        "Regular"),
                new GradingScale(studyPlan, BigDecimal.valueOf(max * 0.6), BigDecimal.valueOf(max * 0.69), "D",
                        "Deficiente"),
                new GradingScale(studyPlan, BigDecimal.valueOf(min), BigDecimal.valueOf(max * 0.59), "F", "Reprobado"));

        gradingScaleRepository.saveAll(scales);
    }

    private void createLetterScale(StudyPlan studyPlan) {
        List<GradingScale> scales = Arrays.asList(
                new GradingScale(studyPlan, BigDecimal.valueOf(18), BigDecimal.valueOf(20), "A", "Excelente"),
                new GradingScale(studyPlan, BigDecimal.valueOf(16), BigDecimal.valueOf(17.99), "B", "Bueno"),
                new GradingScale(studyPlan, BigDecimal.valueOf(14), BigDecimal.valueOf(15.99), "C", "Regular"),
                new GradingScale(studyPlan, BigDecimal.valueOf(12), BigDecimal.valueOf(13.99), "D", "Deficiente"),
                new GradingScale(studyPlan, BigDecimal.valueOf(0), BigDecimal.valueOf(11.99), "F", "Reprobado"));

        gradingScaleRepository.saveAll(scales);
    }

    public String getLetterGrade(StudyPlan studyPlan, Double score) {
        if (score == null)
            return "-";

        GradingScale scale = gradingScaleRepository.findByStudyPlanAndScore(studyPlan.getId(),
                BigDecimal.valueOf(score));
        return scale != null ? scale.getLabel() : "N/A";
    }
}