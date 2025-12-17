package com.school.academic.service;

import com.school.academic.entity.Course;
import com.school.academic.repository.CourseRepository;
import com.school.academic.repository.SectionRepository;
import com.school.academic.repository.GradeRepository;
import com.school.core.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final GradeRepository gradeRepository;
    private final AuditService auditService;

    public CourseService(CourseRepository courseRepository,
                        SectionRepository sectionRepository,
                        GradeRepository gradeRepository,
                        AuditService auditService) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.gradeRepository = gradeRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Course> getAllActiveCourses(@NonNull Pageable pageable) {
        return courseRepository.findAllActive(pageable);
    }

    @Transactional(readOnly = true)
    public List<Course> getAllActiveCourses() {
        return courseRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(@NonNull Long id) {
        return courseRepository.findByIdAndNotDeleted(id);
    }

    public Course saveCourse(@NonNull Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(@NonNull Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
            .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        validateCourseDependencies(id);

        course.setDeleted(true);
        course.setDeletedAt(LocalDateTime.now());
        course.setDeletedBy(getCurrentUser());
        
        courseRepository.save(course);
        auditService.logCourseDeletion(id, getCurrentUser());
    }

    private void validateCourseDependencies(@NonNull Long courseId) {
        long sectionCount = sectionRepository.findByCourseId(courseId).size();
        if (sectionCount > 0) {
            throw new IllegalStateException(
                String.format("No se puede eliminar el curso. Tiene %d sección(es) asociada(s)", sectionCount));
        }

        long gradeCount = gradeRepository.countByCourseId(courseId);
        if (gradeCount > 0) {
            throw new IllegalStateException(
                String.format("No se puede eliminar el curso. Tiene %d calificación(es) registrada(s)", gradeCount));
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}