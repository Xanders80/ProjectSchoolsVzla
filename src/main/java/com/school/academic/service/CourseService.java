package com.school.academic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Course;
import com.school.academic.repository.CourseRepository;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.SectionRepository;
import com.school.core.service.AuditService;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final GradeRepository gradeRepository;
    private final AuditService auditService;
    private final com.school.communication.service.CourseForumService forumService;

    public CourseService(CourseRepository courseRepository,
            SectionRepository sectionRepository,
            GradeRepository gradeRepository,
            AuditService auditService,
            com.school.communication.service.CourseForumService forumService) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.gradeRepository = gradeRepository;
        this.auditService = auditService;
        this.forumService = forumService;
    }

    @Transactional(readOnly = true)
    public Page<Course> getAllActiveCourses(@NonNull Pageable pageable) {
        return courseRepository.findByDeletedFalse(pageable);
    }

    @Transactional(readOnly = true)
    public List<Course> getAllActiveCourses() {
        return courseRepository.findByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(@NonNull Long id) {
        return courseRepository.findByIdAndDeletedFalse(id);
    }

    public Course saveCourse(@NonNull Course course) {
        boolean isNew = course.getId() == null;
        Course saved = courseRepository.save(course);
        if (isNew) {
            forumService.getOrCreateForum(saved);
        }
        return saved;
    }

    public void deleteCourse(@NonNull Long id) {
        Course course = courseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        validateCourseDependencies(id);

        course.setDeleted(true);
        course.setDeletedAt(java.time.LocalDateTime.now());
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
                    String.format("No se puede eliminar el curso. Tiene %d calificación(es) registrada(s)",
                            gradeCount));
        }
    }

    @NonNull
    private String getCurrentUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null) {
            String name = auth.getName();
            if (name != null) {
                return name;
            }
        }
        return "system";
    }
}