package com.school.web.controller.academic;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import com.school.academic.entity.Course;
import com.school.academic.entity.LmsLesson;
import com.school.academic.entity.LmsModule;
import com.school.academic.service.CourseService;
import com.school.academic.service.LmsService;

@Controller
@RequestMapping("/academic/lms")
public class LmsController {

    private static final String COURSE_NOT_FOUND = "Curso no encontrado";
    private static final String LESSON_NOT_FOUND = "Lección no encontrada";

    private final LmsService lmsService;
    private final CourseService courseService;

    public LmsController(LmsService lmsService, CourseService courseService) {
        this.lmsService = lmsService;
        this.courseService = courseService;
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PERSONAL') or hasRole('ADMIN')")
    public String viewCourse(@PathVariable @NonNull Long courseId, Model model) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException(COURSE_NOT_FOUND));

        model.addAttribute("course", course);
        model.addAttribute("modules", lmsService.getModulesByCourse(course, true));

        return "academic/lms/course-view";
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PERSONAL') or hasRole('ADMIN')")
    public String viewLesson(@PathVariable @NonNull Long lessonId, Model model) {
        LmsLesson lesson = lmsService.getLessonById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException(LESSON_NOT_FOUND));

        model.addAttribute("lesson", lesson);
        model.addAttribute("previousLesson", lmsService.getPreviousLesson(lesson).orElse(null));
        model.addAttribute("nextLesson", lmsService.getNextLesson(lesson).orElse(null));

        return "academic/lms/lesson-view";
    }

    @GetMapping("/api/course/{courseId}/modules")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PERSONAL') or hasRole('ADMIN')")
    @ResponseBody
    public List<LmsModule> getCourseModules(@PathVariable @NonNull Long courseId) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException(COURSE_NOT_FOUND));
        return lmsService.getModulesByCourse(course, true);
    }

    @GetMapping("/api/course/{courseId}/search")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PERSONAL') or hasRole('ADMIN')")
    @ResponseBody
    public List<LmsLesson> searchLessons(@PathVariable @NonNull Long courseId, @RequestParam String query) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException(COURSE_NOT_FOUND));
        return lmsService.searchLessonsByCourseAndQuery(course, query);
    }
}
