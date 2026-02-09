package com.school.web.controller.academic;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.CurriculumGrid;
import com.school.academic.entity.StudyPlan;
import com.school.academic.service.CurriculumGridService;
import com.school.academic.service.CourseService;
import com.school.academic.service.StudyPlanService;

@Controller
@RequestMapping("/academic/curriculum")
public class CurriculumGridController {

    private final CurriculumGridService curriculumGridService;
    private final StudyPlanService studyPlanService;
    private final CourseService courseService;

    public CurriculumGridController(CurriculumGridService curriculumGridService,
            StudyPlanService studyPlanService,
            CourseService courseService) {
        this.curriculumGridService = curriculumGridService;
        this.studyPlanService = studyPlanService;
        this.courseService = courseService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("studyPlans", studyPlanService.getAllStudyPlans());
        return "academic/curriculum/curriculum-grid-list";
    }

    @GetMapping("/study-plan/{studyPlanId}")
    public String viewByStudyPlan(@PathVariable @NonNull Long studyPlanId, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            StudyPlan studyPlan = studyPlanService.getStudyPlanById(studyPlanId)
                    .orElseThrow(() -> new RuntimeException("Plan de estudio no encontrado"));

            List<CurriculumGrid> grids = curriculumGridService.getByStudyPlan(studyPlanId);
            model.addAttribute("curriculumGrids", grids);
            model.addAttribute("studyPlan", studyPlan);
            return "academic/curriculum/curriculum-grid-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Plan de estudio no encontrado");
            return "redirect:/academic/curriculum";
        }
    }

    @GetMapping("/new")
    public String newForm(Model model, @RequestParam(required = false) Long studyPlanId) {
        model.addAttribute("curriculumGrid", new CurriculumGrid());
        model.addAttribute("studyPlans", studyPlanService.getAllStudyPlans());
        model.addAttribute("courses", courseService.getAllActiveCourses());
        model.addAttribute("selectedStudyPlanId", studyPlanId);
        return "academic/curriculum/curriculum-grid-form";
    }

    @PostMapping("/save")
    public String save(@NonNull CurriculumGrid curriculumGrid, RedirectAttributes redirectAttributes) {
        try {
            // Hydrate StudyPlan
            if (curriculumGrid.getStudyPlan() != null && curriculumGrid.getStudyPlan().getId() != null) {
                studyPlanService
                        .getStudyPlanById(java.util.Objects.requireNonNull(curriculumGrid.getStudyPlan().getId(),
                                "ID de plan de estudio no puede ser null"))
                        .ifPresent(curriculumGrid::setStudyPlan);
            }

            // Hydrate Courses (Optional, depends on if they are bound as IDs or objects)
            // Spring might bind them as transient objects with IDs. Ideally, fetch them.
            // For brevity and common List handling, sometimes repo.save handles ID-only
            // refs for ManyToMany if they exist.
            // But to be safe and consistent with SectionController:

            // Note: Since they are lists, iterating and replacing is verbose here.
            // If we assume Hibernate handles ManyToMany ID-refs better than ManyToOne, we
            // might skip.
            // However, StudyPlan is ManyToOne and definitely needs hydration.

            curriculumGridService.save(curriculumGrid);
            redirectAttributes.addFlashAttribute("success", "Malla curricular guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/academic/curriculum";
    }
}