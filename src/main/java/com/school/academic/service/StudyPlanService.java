package com.school.academic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.StudyPlan;
import com.school.academic.repository.StudyPlanRepository;

@Service
@Transactional
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;

    public StudyPlanService(StudyPlanRepository studyPlanRepository) {
        this.studyPlanRepository = studyPlanRepository;
    }

    public Page<StudyPlan> getAllStudyPlans(Pageable pageable) {
        return studyPlanRepository.findAll(pageable);
    }

    public List<StudyPlan> getAllStudyPlans() {
        return studyPlanRepository.findAll();
    }

    public Optional<StudyPlan> getStudyPlanById(Long id) {
        return studyPlanRepository.findById(id);
    }

    public StudyPlan saveStudyPlan(StudyPlan studyPlan) {
        return studyPlanRepository.save(studyPlan);
    }

    public void deleteStudyPlan(Long id) {
        studyPlanRepository.deleteById(id);
    }
}
