package com.school.web.controller.academic;

import com.school.academic.entity.Course;
import com.school.academic.repository.CourseRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public String listCourses(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        model.addAttribute("courses", courseRepository.findAll(pageable));
        return "academic/course-list";
    }

    @GetMapping("/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "academic/course-form";
    }

    @PostMapping("/save")
    public String saveCourse(@jakarta.validation.Valid @ModelAttribute @NonNull Course course,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "academic/course-form";
        }
        courseRepository.save(course);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("course", courseRepository.findById(id).orElseThrow());
        return "academic/course-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable @NonNull Long id) {
        courseRepository.deleteById(id);
        return "redirect:/courses";
    }
}
