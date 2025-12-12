package com.school.web.controller.academic;

import com.school.academic.entity.Section;
import com.school.academic.service.AcademicService;
import com.school.admin.service.StaffService;
import com.school.infra.service.InfraService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sections")
public class SectionController {

    private final AcademicService academicService;
    private final StaffService staffService;
    private final InfraService infraService;

    public SectionController(AcademicService academicService, StaffService staffService, InfraService infraService) {
        this.academicService = academicService;
        this.staffService = staffService;
        this.infraService = infraService;
    }

    @GetMapping
    public String listSections(Model model) {
        model.addAttribute("sections", academicService.getAllSections());
        return "academic/section-list";
    }

    @GetMapping("/new")
    public String newSectionForm(Model model) {
        model.addAttribute("section", new Section());
        populateDropdowns(model);
        return "academic/section-form";
    }

    @PostMapping("/save")
    public String saveSection(@ModelAttribute @NonNull Section section, RedirectAttributes redirectAttributes) {
        academicService.saveSection(section);
        redirectAttributes.addFlashAttribute("success", "Sección guardada exitosamente");
        return "redirect:/sections";
    }

    @GetMapping("/edit/{id}")
    public String editSectionForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("section", academicService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id)));
        populateDropdowns(model);
        return "academic/section-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSection(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            academicService.deleteSection(id);
            redirectAttributes.addFlashAttribute("success", "Sección eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar la sección porque tiene registros asociados.");
        }
        return "redirect:/sections";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("courses", academicService.getAllCourses());
        model.addAttribute("teachers", staffService.getAllTeachers());
        model.addAttribute("rooms", infraService.getAllRooms());
    }
}
