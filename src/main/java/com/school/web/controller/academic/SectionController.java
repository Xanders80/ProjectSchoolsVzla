package com.school.web.controller.academic;

import com.school.academic.entity.Section;
import com.school.academic.service.SectionService;
import com.school.academic.repository.CourseRepository;
import com.school.admin.service.StaffService;
import com.school.infra.service.InfraService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sections")
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class SectionController {

    private static final String SECTION_FORM_VIEW = "academic/section-form";
    private final SectionService sectionService;
    private final CourseRepository courseRepository;
    private final StaffService staffService;
    private final InfraService infraService;

    public SectionController(SectionService sectionService, CourseRepository courseRepository,
            StaffService staffService, InfraService infraService) {
        this.sectionService = sectionService;
        this.courseRepository = courseRepository;
        this.staffService = staffService;
        this.infraService = infraService;
    }

    @GetMapping
    public String listSections(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        model.addAttribute("sections", sectionService.getAllActiveSections(pageable));
        return "academic/section-list";
    }

    @GetMapping("/new")
    public String newSectionForm(Model model) {
        model.addAttribute("section", new Section());
        populateDropdowns(model);
        return SECTION_FORM_VIEW;
    }

    @PostMapping
    public String saveSection(@jakarta.validation.Valid @ModelAttribute @NonNull Section section,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors() || section.getCourse() == null) {
            if (section.getCourse() == null) {
                result.rejectValue("course", "NotNull", "El curso es obligatorio");
            }
            populateDropdowns(model);
            return SECTION_FORM_VIEW;
        }
        sectionService.saveSection(section);
        return "redirect:/sections";
    }

    @GetMapping("/edit/{id}")
    public String editSectionForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("section", sectionService.getSectionById(id).orElseThrow());
        populateDropdowns(model);
        return SECTION_FORM_VIEW;
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteSection(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            sectionService.deleteSection(id);
            redirectAttributes.addFlashAttribute("successMessage", "Sección eliminada exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sección no encontrada");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del sistema");
        }
        return "redirect:/sections";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("teachers", staffService.getAllTeachers());
        model.addAttribute("rooms", infraService.getAllRooms());
    }
}
