package com.school.academic.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Grade;
import com.school.academic.entity.Student;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.StudentRepository;

@Service
@Transactional
public class PromotionService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final com.school.core.service.AuditService auditService;

    public PromotionService(GradeRepository gradeRepository,
            StudentRepository studentRepository,
            com.school.core.service.AuditService auditService) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.auditService = auditService;
    }

    /**
     * Calcula el estado de promoción de un estudiante para un periodo específico.
     * Criterio: Promedio general >= 70 y no más de 2 materias reprobadas (< 60).
     */
    public PromotionResult evaluatePromotion(@NonNull Long studentId, @NonNull Long periodId) {
        List<Grade> grades = gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(studentId).stream()
                .filter(g -> g.getPeriod() != null && g.getPeriod().getId().equals(periodId))
                .collect(Collectors.toList());

        if (grades.isEmpty()) {
            return new PromotionResult(studentId, "NO_DATA", 0.0, 0);
        }

        double average = grades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
        long failedCourses = grades.stream().filter(g -> g.getScore() < 60.0).count();

        String status = (average >= 70.0 && failedCourses <= 2) ? "PROMOTED"
                : (failedCourses > 4) ? "RETAINED" : "PENDING_RECOVERY";

        return new PromotionResult(studentId, status, average, (int) failedCourses);
    }

    /**
     * Cierra el periodo académico y procesa las promociones masivamente.
     */
    public void processMassPromotion(@NonNull Long periodId) {
        List<Student> activeStudents = studentRepository.findAllActive();

        for (Student student : activeStudents) {
            PromotionResult result = evaluatePromotion(student.getId(), periodId);
            // Aquí se podría actualizar una entidad 'AcademicHistory' o similar si
            // existiera
            // Por ahora registramos en auditoría y log
            auditService.logGenericAction("ACADEMIC_PROMOTION",
                    "Student " + student.getId() + " evaluation: " + result.getStatus(),
                    "system");
        }
    }

    public static class PromotionResult {
        private final Long studentId;
        private final String status;
        private final double average;
        private final int failedCourses;

        public PromotionResult(Long studentId, String status, double average, int failedCourses) {
            this.studentId = studentId;
            this.status = status;
            this.average = average;
            this.failedCourses = failedCourses;
        }

        public Long getStudentId() {
            return studentId;
        }

        public String getStatus() {
            return status;
        }

        public double getAverage() {
            return average;
        }

        public int getFailedCourses() {
            return failedCourses;
        }
    }
}
