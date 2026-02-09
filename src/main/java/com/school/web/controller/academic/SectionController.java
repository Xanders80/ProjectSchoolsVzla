package com.school.web.controller.academic;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import com.school.academic.entity.Section;
import com.school.academic.repository.AcademicPeriodRepository;
import com.school.academic.repository.CourseRepository;
import com.school.academic.service.SectionService;
import com.school.admin.service.StaffService;
import com.school.core.controller.BaseDeleteController;
import com.school.core.validation.ValidId;
import com.school.infra.service.InfraService;

@Controller
@RequestMapping("/sections")
@Validated
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class SectionController extends BaseDeleteController {

    private static final String SECTION_FORM_VIEW = "academic/section-form";
    private final SectionService sectionService;
    private final CourseRepository courseRepository;
    private final StaffService staffService;
    private final InfraService infraService;
    private final AcademicPeriodRepository academicPeriodRepository;

    public SectionController(SectionService sectionService, CourseRepository courseRepository,
            StaffService staffService, InfraService infraService,
            AcademicPeriodRepository academicPeriodRepository) {
        this.sectionService = sectionService;
        this.courseRepository = courseRepository;
        this.staffService = staffService;
        this.infraService = infraService;
        this.academicPeriodRepository = academicPeriodRepository;
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

        // 1. Basic Validation
        if (result.hasErrors()) {
            populateDropdowns(model);
            return SECTION_FORM_VIEW;
        }

        // 2. Strict Relationship Validation & Hydration

        // Validate Course
        if (section.getCourse() == null || section.getCourse().getId() == null) {
            result.rejectValue("course", "NotNull", "El curso es obligatorio");
        } else {
            courseRepository
                    .findById(java.util.Objects.requireNonNull(section.getCourse().getId(),
                            "ID de curso no puede ser null"))
                    .ifPresentOrElse(section::setCourse,
                            () -> result.rejectValue("course", "NotFound", "El curso seleccionado no existe"));
        }

        // Validate Period
        if (section.getPeriod() == null || section.getPeriod().getId() == null) {
            result.rejectValue("period", "NotNull", "El periodo académico es obligatorio");
        } else {
            academicPeriodRepository
                    .findById(java.util.Objects.requireNonNull(section.getPeriod().getId(),
                            "ID de periodo no puede ser null"))
                    .ifPresentOrElse(section::setPeriod,
                            () -> result.rejectValue("period", "NotFound", "El periodo seleccionado no existe"));
        }

        if (result.hasErrors()) {
            populateDropdowns(model);
            return SECTION_FORM_VIEW;
        }

        // 3. Optional Relationships Hydration
        if (section.getTeacher() != null && section.getTeacher().getId() != null) {
            staffService
                    .getStaffById(java.util.Objects.requireNonNull(section.getTeacher().getId(),
                            "ID de docente no puede ser null"))
                    .ifPresent(section::setTeacher);
        } else {
            section.setTeacher(null);
        }

        if (section.getRoom() != null && section.getRoom().getId() != null) {
            infraService
                    .getRoomById(
                            java.util.Objects.requireNonNull(section.getRoom().getId(), "ID de aula no puede ser null"))
                    .ifPresent(section::setRoom);
        } else {
            section.setRoom(null);
        }

        // 4. Save
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
    public String deleteSection(@PathVariable @ValidId String id,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            Long sectionId = Long.parseLong(id);
            sectionService.deleteSection(sectionId);
            logDeleteAttempt("Section", id, request, true, null);
            handleDeleteResult(true, "Sección eliminada exitosamente", null, redirectAttributes);
        } catch (IllegalArgumentException e) {
            logDeleteAttempt("Section", id, request, false, "Not found");
            handleDeleteResult(false, null, "Sección no encontrada", redirectAttributes);
        } catch (IllegalStateException e) {
            logDeleteAttempt("Section", id, request, false, e.getMessage());
            handleDeleteResult(false, null, e.getMessage(), redirectAttributes);
        } catch (Exception e) {
            logDeleteAttempt("Section", id, request, false, e.getMessage());
            handleDeleteResult(false, null, "Error interno del sistema", redirectAttributes);
        }
        return "redirect:/sections";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("teachers", staffService.getAllTeachers());
        model.addAttribute("rooms", infraService.getAllRooms());
        model.addAttribute("periods", academicPeriodRepository.findAll());
    }
}
