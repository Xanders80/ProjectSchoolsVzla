package com.school.academic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.dto.StudyPlanSummary;
import com.school.academic.entity.CurriculumGrid;
import com.school.academic.entity.StudyPlan;
import com.school.academic.repository.CurriculumGridRepository;
import com.school.academic.repository.GradingScaleRepository;
import com.school.academic.repository.StudyPlanRepository;

@Service
@Transactional
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final CurriculumGridRepository curriculumGridRepository;
    private final GradingScaleRepository gradingScaleRepository;

    public StudyPlanService(StudyPlanRepository studyPlanRepository,
            CurriculumGridRepository curriculumGridRepository,
            GradingScaleRepository gradingScaleRepository) {
        this.studyPlanRepository = studyPlanRepository;
        this.curriculumGridRepository = curriculumGridRepository;
        this.gradingScaleRepository = gradingScaleRepository;
    }

    @Transactional(readOnly = true)
    public Page<StudyPlan> getAllStudyPlans(@NonNull Pageable pageable) {
        return studyPlanRepository.findByActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public List<StudyPlan> getAllStudyPlans() {
        return studyPlanRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Optional<StudyPlan> getStudyPlanById(@NonNull Long id) {
        return studyPlanRepository.findByIdAndActiveTrue(id);
    }

    public StudyPlan saveStudyPlan(@NonNull StudyPlan studyPlan) {
        validateStudyPlan(studyPlan);
        return studyPlanRepository.save(studyPlan);
    }

    public StudyPlan createCompleteStudyPlan(@NonNull StudyPlan studyPlan,
            com.school.academic.enums.ScaleType scaleType) {
        // Guardar plan
        StudyPlan saved = saveStudyPlan(studyPlan);

        // Crear escala de calificación por defecto
        if (scaleType != null) {
            createDefaultGradingScale(saved, scaleType);
        }

        return saved;
    }

    public void deleteStudyPlan(@NonNull Long id) {
        StudyPlan studyPlan = studyPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan de estudio no encontrado"));

        validateDeletion(id);

        // Soft delete
        studyPlan.setActive(false);
        studyPlanRepository.save(studyPlan);
    }

    public void forceDeleteStudyPlan(@NonNull Long id) {
        StudyPlan studyPlan = studyPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan de estudio no encontrado"));

        // Eliminar dependencias en orden
        gradingScaleRepository.deleteByStudyPlan(studyPlan);
        curriculumGridRepository.deleteAll(curriculumGridRepository.findByStudyPlanId(id));

        // Soft delete del plan
        studyPlan.setActive(false);
        studyPlanRepository.save(studyPlan);
    }

    @Transactional(readOnly = true)
    public StudyPlanSummary getStudyPlanSummary(@NonNull Long id) {
        StudyPlan plan = getStudyPlanById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));

        List<CurriculumGrid> grids = curriculumGridRepository.findByStudyPlanId(id);
        int totalCourses = grids.stream()
                .mapToInt(grid -> grid.getRequiredCourses().size() + grid.getElectiveCourses().size())
                .sum();

        int totalHours = grids.stream()
                .mapToInt(grid -> grid.getTotalHours() != null ? grid.getTotalHours() : 0)
                .sum();

        return new StudyPlanSummary(plan, grids.size(), totalCourses, totalHours);
    }

    private void validateStudyPlan(StudyPlan studyPlan) {
        if (studyPlan.getName() == null || studyPlan.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del plan es obligatorio");
        }

        // Verificar nombre único
        if (studyPlan.getId() == null) {
            if (studyPlanRepository.existsByNameAndActiveTrue(studyPlan.getName())) {
                throw new IllegalArgumentException("Ya existe un plan con ese nombre");
            }
        }
    }

    private void validateDeletion(@NonNull Long id) {
        // Verificar mallas curriculares
        long curriculumCount = curriculumGridRepository.countByStudyPlanId(id);
        if (curriculumCount > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar el plan. Tiene " + curriculumCount + " mallas curriculares asociadas");
        }

        // Verificar cursos directos
        StudyPlan plan = studyPlanRepository.findById(id).orElse(null);
        if (plan != null && !plan.getCourses().isEmpty()) {
            throw new IllegalStateException(
                    "No se puede eliminar el plan. Tiene " + plan.getCourses().size() + " cursos asociados");
        }

        // Verificar escalas de calificación
        long scaleCount = gradingScaleRepository.countByStudyPlanId(id);
        if (scaleCount > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar el plan. Tiene " + scaleCount + " escalas de calificación asociadas");
        }
    }

    private void createDefaultGradingScale(StudyPlan studyPlan, com.school.academic.enums.ScaleType scaleType) {
        com.school.academic.service.GradingScaleConfigService configService = new com.school.academic.service.GradingScaleConfigService(
                gradingScaleRepository);
        configService.setupScale(studyPlan, scaleType);
    }
}
