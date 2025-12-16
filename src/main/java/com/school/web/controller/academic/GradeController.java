package com.school.web.controller.academic;

import com.school.academic.entity.Grade;
import com.school.academic.enums.EvaluationType;
import com.school.academic.service.AcademicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/grades")
public class GradeController {

    private final AcademicService academicService;

    public GradeController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping
    public String listGrades(Model model) {
        model.addAttribute("grades", academicService.getAllGrades());
        return "academic/grade-list";
    }

    @GetMapping("/new")
    public String newGradeForm(Model model) {
        model.addAttribute("grade", new Grade());
        populateDropdowns(model);
        return "academic/grade-form";
    }

    @PostMapping
    public String saveGrade(@ModelAttribute Grade grade, RedirectAttributes redirectAttributes) {
        academicService.saveGrade(grade);
        redirectAttributes.addFlashAttribute("success", "Calificación guardada exitosamente");
        return "redirect:/grades";
    }

    @GetMapping("/edit/{id}")
    public String editGradeForm(@PathVariable Long id, Model model) {
        model.addAttribute("grade", academicService.getGradeById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid grade Id:" + id)));
        populateDropdowns(model);
        return "academic/grade-form";
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteGrade(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        academicService.deleteGrade(id);
        redirectAttributes.addFlashAttribute("success", "Calificación eliminada exitosamente");
        return "redirect:/grades";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("students",
                academicService.getAllStudents(org.springframework.data.domain.Pageable.unpaged()).getContent());
        model.addAttribute("courses", academicService.getAllCourses());
        model.addAttribute("evaluationTypes", EvaluationType.values());
    }
}
