package com.school.academic.controller;

import com.school.academic.entity.TeacherDevelopment;
import com.school.academic.service.TeacherDevelopmentService;
// ...existing code...
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/academic/desarrollo-docente")
public class TeacherDevelopmentController {
    private final TeacherDevelopmentService teacherDevelopmentService;

    public TeacherDevelopmentController(TeacherDevelopmentService teacherDevelopmentService) {
        this.teacherDevelopmentService = teacherDevelopmentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("desarrollos", teacherDevelopmentService.findAll());
        return "academic/teacher-development-list";
    }

    @GetMapping("/nuevo")
    public String form(Model model) {
        model.addAttribute("teacherDevelopment", new TeacherDevelopment());
        return "academic/teacher-development-form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute TeacherDevelopment teacherDevelopment, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("teacherDevelopment", teacherDevelopment);
            return "academic/teacher-development-form";
        }
        
        if (teacherDevelopment.getTeacherProfile() == null) {
            teacherDevelopment.setTeacherProfile(new com.school.academic.entity.TeacherProfile());
        }
        
        teacherDevelopmentService
                .save(java.util.Objects.requireNonNull(teacherDevelopment, "El desarrollo docente no puede ser null"));
        return "redirect:/academic/desarrollo-docente";
    }

    @GetMapping("/eliminar/{id}")
    public String delete(@PathVariable Long id) {
        teacherDevelopmentService.delete(java.util.Objects.requireNonNull(id, "El ID no puede ser null"));
        return "redirect:/academic/desarrollo-docente";
    }
}
