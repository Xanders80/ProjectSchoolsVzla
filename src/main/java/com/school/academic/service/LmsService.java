package com.school.academic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Course;
import com.school.academic.entity.LmsLesson;
import com.school.academic.entity.LmsModule;
import com.school.academic.repository.LmsLessonRepository;
import com.school.academic.repository.LmsModuleRepository;

@Service
public class LmsService {

    private final LmsModuleRepository moduleRepository;
    private final LmsLessonRepository lessonRepository;

    public LmsService(LmsModuleRepository moduleRepository, LmsLessonRepository lessonRepository) {
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
    }

    @Transactional(readOnly = true)
    public List<LmsModule> getModulesByCourse(Course course, boolean onlyPublished) {
        if (onlyPublished) {
            return moduleRepository.findByCourseAndPublishedTrueOrderBySortOrderAsc(course);
        }
        return moduleRepository.findByCourseOrderBySortOrderAsc(course);
    }

    @Transactional
    @NonNull
    public LmsModule saveModule(@NonNull LmsModule module) {
        return moduleRepository.save(module);
    }

    @Transactional(readOnly = true)
    public List<LmsLesson> getLessonsByModule(LmsModule module, boolean onlyPublished) {
        if (onlyPublished) {
            return lessonRepository.findByModuleAndPublishedTrueOrderBySortOrderAsc(module);
        }
        return lessonRepository.findByModuleOrderBySortOrderAsc(module);
    }

    @Transactional(readOnly = true)
    public Optional<LmsLesson> getLessonById(@NonNull Long lessonId) {
        return lessonRepository.findById(lessonId);
    }

    @Transactional(readOnly = true)
    public Optional<LmsLesson> getPreviousLesson(LmsLesson currentLesson) {
        List<LmsLesson> lessons = getLessonsByModule(currentLesson.getModule(), true);
        int currentIndex = lessons.indexOf(currentLesson);
        if (currentIndex > 0) {
            return Optional.of(lessons.get(currentIndex - 1));
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<LmsLesson> getNextLesson(LmsLesson currentLesson) {
        List<LmsLesson> lessons = getLessonsByModule(currentLesson.getModule(), true);
        int currentIndex = lessons.indexOf(currentLesson);
        if (currentIndex >= 0 && currentIndex < lessons.size() - 1) {
            return Optional.of(lessons.get(currentIndex + 1));
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<LmsLesson> searchLessonsByCourseAndQuery(Course course, String query) {
        List<LmsModule> modules = getModulesByCourse(course, true);
        List<LmsLesson> results = new ArrayList<>();
        for (LmsModule module : modules) {
            List<LmsLesson> lessons = getLessonsByModule(module, true);
            for (LmsLesson lesson : lessons) {
                if (lesson.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        (lesson.getContent() != null
                                && lesson.getContent().toLowerCase().contains(query.toLowerCase()))) {
                    results.add(lesson);
                }
            }
        }
        return results;
    }

    @Transactional
    @NonNull
    public LmsLesson saveLesson(@NonNull LmsLesson lesson) {
        return lessonRepository.save(lesson);
    }
}
