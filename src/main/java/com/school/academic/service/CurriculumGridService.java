package com.school.academic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.CurriculumGrid;
import com.school.academic.repository.CurriculumGridRepository;

@Service
@Transactional
public class CurriculumGridService {

    private final CurriculumGridRepository curriculumGridRepository;

    public CurriculumGridService(CurriculumGridRepository curriculumGridRepository) {
        this.curriculumGridRepository = curriculumGridRepository;
    }

    public List<CurriculumGrid> getByStudyPlan(Long studyPlanId) {
        return curriculumGridRepository.findByStudyPlanId(studyPlanId);
    }

    public Optional<CurriculumGrid> getByStudyPlanAndGrade(Long studyPlanId, Integer gradeLevel) {
        return Optional.ofNullable(curriculumGridRepository.findByStudyPlanAndGradeLevel(studyPlanId, gradeLevel));
    }

    public CurriculumGrid save(@NonNull CurriculumGrid curriculumGrid) {
        calculateTotalHours(curriculumGrid);
        return curriculumGridRepository.save(curriculumGrid);
    }

    public void delete(@NonNull Long id) {
        curriculumGridRepository.deleteById(id);
    }

    private void calculateTotalHours(CurriculumGrid grid) {
        int totalHours = 0;

        if (grid.getRequiredCourses() != null) {
            totalHours += grid.getRequiredCourses().stream()
                    .mapToInt(course -> course.getCredits() != null ? course.getCredits() : 0)
                    .sum();
        }

        if (grid.getElectiveCourses() != null) {
            totalHours += grid.getElectiveCourses().stream()
                    .mapToInt(course -> course.getCredits() != null ? course.getCredits() : 0)
                    .sum();
        }

        grid.setTotalHours(totalHours);
    }
}