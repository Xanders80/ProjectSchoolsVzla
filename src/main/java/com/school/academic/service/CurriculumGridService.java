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
    // Métodos CRUD para el controlador
    public List<CurriculumGrid> findAll() {
        return curriculumGridRepository.findAll();
    }

    public CurriculumGrid save(@NonNull CurriculumGrid curriculumGrid) {
        // Validación de campos obligatorios
        if (curriculumGrid.getGradeLevel() == null) {
            throw new IllegalArgumentException("El nivel de grado es obligatorio.");
        }
        if (curriculumGrid.getStudyPlan() == null) {
            throw new IllegalArgumentException("El plan de estudios es obligatorio.");
        }
        // Validación de unicidad por plan y grado
        boolean exists = curriculumGridRepository.findAll().stream()
                .anyMatch(grid -> grid.getStudyPlan().equals(curriculumGrid.getStudyPlan()) &&
                        grid.getGradeLevel().equals(curriculumGrid.getGradeLevel()) &&
                        (curriculumGrid.getId() == null || !grid.getId().equals(curriculumGrid.getId())));
        if (exists) {
            throw new IllegalArgumentException("Ya existe una malla curricular para ese plan y grado.");
        }
        calculateTotalHours(curriculumGrid);
        return curriculumGridRepository.save(curriculumGrid);
    }

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

    // ...existing code...

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