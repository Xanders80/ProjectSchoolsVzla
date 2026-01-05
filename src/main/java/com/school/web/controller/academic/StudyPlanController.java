package com.school.web.controller.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.StudyPlan;
import com.school.academic.enums.AcademicLevel;
import com.school.academic.service.StudyPlanService;
import com.school.academic.entity.GradingScale;
import com.school.academic.dto.StudyPlanSummary;
import com.school.academic.enums.ScaleType;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/academic/study-plans")
public class StudyPlanController {

    private static final String FORM_VIEW = "academic/study-plan-form";
    private static final String LIST_VIEW = "academic/study-plan-list";
    private static final String DETAIL_VIEW = "academic/study-plan-detail";

    private final StudyPlanService studyPlanService;
    private final com.school.academic.service.GradingScaleService gradingScaleService;
    private final com.school.academic.service.GradingScaleConfigService gradingScaleConfigService;

    public StudyPlanController(StudyPlanService studyPlanService,
            com.school.academic.service.GradingScaleService gradingScaleService,
            com.school.academic.service.GradingScaleConfigService gradingScaleConfigService) {
        this.studyPlanService = studyPlanService;
        this.gradingScaleService = gradingScaleService;
        this.gradingScaleConfigService = gradingScaleConfigService;
    }

    @GetMapping
    public String listStudyPlans(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<StudyPlan> studyPlans = studyPlanService.getAllStudyPlans(pageable);
        model.addAttribute("studyPlans", studyPlans);
        model.addAttribute("academicLevels", AcademicLevel.values());
        return LIST_VIEW;
    }

    @GetMapping("/new")
    public String newStudyPlanForm(Model model) {
        model.addAttribute("studyPlan", new StudyPlan());
        model.addAttribute("academicLevels", AcademicLevel.values());
        model.addAttribute("scaleTypes", ScaleType.values());
        return FORM_VIEW;
    }

    @PostMapping
    public String saveStudyPlan(@jakarta.validation.Valid @ModelAttribute @NonNull StudyPlan studyPlan,
            BindingResult result,
            @RequestParam(required = false) ScaleType scaleType,
            Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            addCommonAttributes(model);
            return FORM_VIEW;
        }

        try {
            StudyPlan saved;
            if (scaleType != null) {
                saved = studyPlanService.createCompleteStudyPlan(studyPlan, scaleType);
            } else {
                saved = studyPlanService.saveStudyPlan(studyPlan);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Plan de estudio guardado exitosamente");
            return "redirect:/academic/study-plans/" + saved.getId();
        } catch (Exception e) {
            addCommonAttributes(model);
            model.addAttribute("errorMessage", "Error al guardar: " + e.getMessage());
            return FORM_VIEW;
        }
    }

    @GetMapping("/{id}")
    public String viewStudyPlan(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            StudyPlanSummary summary = studyPlanService.getStudyPlanSummary(id);
            model.addAttribute("summary", summary);
            model.addAttribute("gradingScales", gradingScaleService.getScalesByStudyPlanId(id));
            return DETAIL_VIEW;
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Plan de estudio no encontrado");
            return "redirect:/academic/study-plans";
        }
    }

    @GetMapping("/edit/{id}")
    public String editStudyPlan(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            StudyPlan studyPlan = studyPlanService.getStudyPlanById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Plan de estudio no encontrado con id: " + id));
            model.addAttribute("studyPlan", studyPlan);
            model.addAttribute("academicLevels", AcademicLevel.values());
            model.addAttribute("gradingScales", gradingScaleService.getScalesByStudyPlanId(id));
            model.addAttribute("newScale", new GradingScale());
            return FORM_VIEW;
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Plan de estudio no encontrado");
            return "redirect:/academic/study-plans";
        }
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteStudyPlan(@PathVariable @NonNull Long id,
            @RequestParam(defaultValue = "false") boolean force,
            RedirectAttributes redirectAttributes) {
        try {
            if (force) {
                studyPlanService.forceDeleteStudyPlan(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Plan de estudio y dependencias eliminados exitosamente");
            } else {
                studyPlanService.deleteStudyPlan(id);
                redirectAttributes.addFlashAttribute("successMessage", "Plan de estudio eliminado exitosamente");
            }
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Plan de estudio no encontrado");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("showForceOption", true);
            redirectAttributes.addFlashAttribute("studyPlanId", id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del sistema");
        }
        return "redirect:/academic/study-plans";
    }

    @PostMapping("/{id}/scales")
    public String addGradingScale(@PathVariable @NonNull Long id,
            @ModelAttribute GradingScale newScale,
            RedirectAttributes redirectAttributes) {
        try {
            StudyPlan plan = studyPlanService.getStudyPlanById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Plan de estudio no encontrado con id: " + id));
            newScale.setStudyPlan(plan);
            gradingScaleService.saveGradingScale(newScale);
            redirectAttributes.addFlashAttribute("successMessage", "Escala agregada correctamente");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Plan de estudio no encontrado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al agregar escala: " + e.getMessage());
        }
        return "redirect:/academic/study-plans/edit/" + id;
    }

    @PostMapping("/scales/delete/{scaleId}")
    public String deleteGradingScale(@PathVariable @NonNull Long scaleId, @RequestParam @NonNull Long studyPlanId,
            RedirectAttributes redirectAttributes) {
        try {
            gradingScaleService.deleteGradingScale(scaleId);
            redirectAttributes.addFlashAttribute("successMessage", "Escala eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar escala: " + e.getMessage());
        }
        return "redirect:/academic/study-plans/edit/" + studyPlanId;
    }

    // Método auxiliar para reducir duplicación
    private void addCommonAttributes(Model model) {
        model.addAttribute("academicLevels", AcademicLevel.values());
        model.addAttribute("scaleTypes", ScaleType.values());
    }
}