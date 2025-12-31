package com.school.academic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.GradingScale;
import com.school.academic.repository.GradingScaleRepository;

@Service
@Transactional
public class GradingScaleService {

    private final GradingScaleRepository gradingScaleRepository;

    public GradingScaleService(GradingScaleRepository gradingScaleRepository) {
        this.gradingScaleRepository = gradingScaleRepository;
    }

    public List<GradingScale> getScalesByStudyPlanId(Long studyPlanId) {
        return gradingScaleRepository.findByStudyPlanId(studyPlanId);
    }

    public GradingScale saveGradingScale(GradingScale gradingScale) {
        return gradingScaleRepository.save(gradingScale);
    }

    public void deleteGradingScale(Long id) {
        gradingScaleRepository.deleteById(id);
    }
}
