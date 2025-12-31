package com.school.web.controller.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.StudyPlan;
import com.school.academic.enums.AcademicLevel;
import com.school.academic.service.StudyPlanService;

@Controller
@RequestMapping("/academic/study-plans")
public class StudyPlanController {

    private static final String FORM_VIEW = "academic/study-plan-form";
    private static final String LIST_VIEW = "academic/study-plan-list";

    private final StudyPlanService studyPlanService;
    private final com.school.academic.service.GradingScaleService gradingScaleService;

    public StudyPlanController(StudyPlanService studyPlanService,
            com.school.academic.service.GradingScaleService gradingScaleService) {
        this.studyPlanService = studyPlanService;
        this.gradingScaleService = gradingScaleService;
    }

    @GetMapping
    public String listStudyPlans(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<StudyPlan> studyPlans = studyPlanService.getAllStudyPlans(pageable);
        model.addAttribute("studyPlans", studyPlans);
        return LIST_VIEW;
    }

    @GetMapping("/new")
    public String newStudyPlanForm(Model model) {
        model.addAttribute("studyPlan", new StudyPlan());
        model.addAttribute("academicLevels", AcademicLevel.values());
        return FORM_VIEW;
    }

    @PostMapping
    public String saveStudyPlan(@jakarta.validation.Valid @ModelAttribute @NonNull StudyPlan studyPlan,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("academicLevels", AcademicLevel.values());
            return FORM_VIEW;
        }
        try {
            studyPlanService.saveStudyPlan(studyPlan);
            redirectAttributes.addFlashAttribute("successMessage", "Plan de estudio guardado exitosamente");
        } catch (Exception e) {
            model.addAttribute("academicLevels", AcademicLevel.values());
            model.addAttribute("errorMessage", "Error al guardar el plan de estudio: " + e.getMessage());
            return FORM_VIEW;
        }
        return "redirect:/academic/study-plans";
    }

    @GetMapping("/edit/{id}")
    public String editStudyPlanForm(@PathVariable @NonNull Long id, Model model,
            RedirectAttributes redirectAttributes) {
        return studyPlanService.getStudyPlanById(id)
                .map(plan -> {
                    model.addAttribute("studyPlan", plan);
                    model.addAttribute("academicLevels", AcademicLevel.values());
                    // Fetch existing scales
                    model.addAttribute("gradingScales", gradingScaleService.getScalesByStudyPlanId(id));
                    // Empty scale for form
                    model.addAttribute("newScale", new com.school.academic.entity.GradingScale());
                    return FORM_VIEW;
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Plan de estudio no encontrado");
                    return "redirect:/academic/study-plans";
                });
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteStudyPlan(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            studyPlanService.deleteStudyPlan(id);
            redirectAttributes.addFlashAttribute("successMessage", "Plan de estudio eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No se puede eliminar el plan de estudio, es posible que tenga cursos asociados.");
        }
        return "redirect:/academic/study-plans";
    }

    @PostMapping("/{id}/scales")
    public String addGradingScale(@PathVariable Long id,
            @ModelAttribute com.school.academic.entity.GradingScale newScale,
            RedirectAttributes redirectAttributes) {
        try {
            StudyPlan plan = studyPlanService.getStudyPlanById(id).orElseThrow();
            newScale.setStudyPlan(plan);
            gradingScaleService.saveGradingScale(newScale);
            redirectAttributes.addFlashAttribute("successMessage", "Escala agregada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al agregar escala: " + e.getMessage());
        }
        return "redirect:/academic/study-plans/edit/" + id;
    }

    @PostMapping("/scales/delete/{scaleId}")
    public String deleteGradingScale(@PathVariable Long scaleId, @RequestParam Long studyPlanId,
            RedirectAttributes redirectAttributes) {
        try {
            gradingScaleService.deleteGradingScale(scaleId);
            redirectAttributes.addFlashAttribute("successMessage", "Escala eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar escala: " + e.getMessage());
        }
        return "redirect:/academic/study-plans/edit/" + studyPlanId;
    }
}
