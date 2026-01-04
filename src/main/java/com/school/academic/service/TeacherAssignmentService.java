package com.school.academic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.AcademicPeriod;
import com.school.academic.entity.Course;
import com.school.academic.entity.TeacherAssignment;
import com.school.academic.entity.TeacherProfile;
import com.school.academic.repository.TeacherAssignmentRepository;
import com.school.academic.repository.TeacherProfileRepository;

@Service
@Transactional
public class TeacherAssignmentService {

    private final TeacherAssignmentRepository assignmentRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    public TeacherAssignmentService(TeacherAssignmentRepository assignmentRepository,
            TeacherProfileRepository teacherProfileRepository) {
        this.assignmentRepository = assignmentRepository;
        this.teacherProfileRepository = teacherProfileRepository;
    }

    public TeacherAssignment assignTeacher(@NonNull Long teacherProfileId, Course course, AcademicPeriod period) {
        // Validar que no exista asignación duplicada
        if (assignmentRepository.existsByTeacherProfileIdAndCourseIdAndAcademicPeriodIdAndIsActiveTrue(
                teacherProfileId, course.getId(), period.getId())) {
            throw new IllegalArgumentException("El docente ya está asignado a este curso en el período");
        }

        TeacherProfile teacher = teacherProfileRepository.findById(teacherProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil docente no encontrado"));

        // Validar carga académica
        validateTeacherWorkload(teacher, course.getCredits());

        TeacherAssignment assignment = new TeacherAssignment();
        assignment.setTeacherProfile(teacher);
        assignment.setCourse(course);
        assignment.setAcademicPeriod(period);
        assignment.setAssignedHours(course.getCredits());
        assignment.setCompatibilityScore(calculateCompatibility(teacher, course));

        return assignmentRepository.save(assignment);
    }

    public Optional<TeacherProfile> findBestTeacherForCourse(Course course) {
        List<TeacherProfile> availableTeachers = teacherProfileRepository.findAll();

        return availableTeachers.stream()
                .filter(teacher -> isTeacherAvailable(teacher, course.getCredits()))
                .max((t1, t2) -> Double.compare(
                        calculateCompatibility(t1, course),
                        calculateCompatibility(t2, course)));
    }

    private void validateTeacherWorkload(TeacherProfile teacher, Integer additionalHours) {
        Integer currentHours = assignmentRepository.getTotalHoursByTeacher(teacher.getId());
        currentHours = currentHours != null ? currentHours : 0;

        Integer maxHours = teacher.getMaxHoursPerWeek() != null ? teacher.getMaxHoursPerWeek() : 40;

        if (currentHours + additionalHours > maxHours) {
            throw new IllegalArgumentException("El docente excedería su carga máxima de horas");
        }
    }

    private boolean isTeacherAvailable(TeacherProfile teacher, Integer requiredHours) {
        try {
            validateTeacherWorkload(teacher, requiredHours);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Double calculateCompatibility(TeacherProfile teacher, Course course) {
        double score = 0.0;

        // Compatibilidad por especialización
        if (teacher.getSpecializationArea() != null && course.getName() != null) {
            if (course.getName().toLowerCase().contains(teacher.getSpecializationArea().toLowerCase())) {
                score += 50.0;
            }
        }

        // Compatibilidad por materias preferidas
        if (teacher.getPreferredSubjects() != null && course.getName() != null) {
            if (teacher.getPreferredSubjects().toLowerCase().contains(course.getName().toLowerCase())) {
                score += 30.0;
            }
        }

        // Puntuación por evaluación
        if (teacher.getEvaluationScore() != null) {
            score += teacher.getEvaluationScore() * 2; // Máximo 20 puntos
        }

        return Math.min(score, 100.0);
    }

    public List<TeacherAssignment> getAssignmentsByTeacher(@NonNull Long teacherProfileId) {
        return assignmentRepository.findByTeacherProfileIdAndIsActiveTrue(teacherProfileId);
    }

    public void deactivateAssignment(@NonNull Long assignmentId) {
        TeacherAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada"));
        assignment.setIsActive(false);
        assignmentRepository.save(assignment);
    }
}