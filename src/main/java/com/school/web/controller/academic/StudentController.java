package com.school.web.controller.academic;

import com.school.academic.entity.Student;
import com.school.academic.service.AcademicService;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final AcademicService academicService;

    public StudentController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping
    public String listStudents(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        org.springframework.data.domain.Page<Student> studentPage = academicService.getAllStudents(pageable);
        model.addAttribute("students", studentPage);
        return "academic/student-list";
    }

    @GetMapping("/new")
    public String newStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "academic/student-form";
    }

    @PostMapping("/save")
    public String saveStudent(@jakarta.validation.Valid @ModelAttribute @NonNull Student student,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "academic/student-form";
        }
        academicService.saveStudent(student);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable @NonNull Long id, Model model) {
        AcademicService service = this.academicService;
        // Note: Using lambda or Optional check wrapper is better practice, direct get()
        // is risky but acceptable for prototype
        model.addAttribute("student",
                service.getStudentById(id).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id)));
        return "academic/student-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable @NonNull Long id) {
        academicService.deleteStudent(id);
        return "redirect:/students";
    }
}
