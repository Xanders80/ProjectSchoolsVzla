package com.school.web.controller.admin;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.service.AcademicService;
import com.school.core.entity.Parent;
import com.school.core.service.ParentService;

@Controller
@RequestMapping("/parents")
public class ParentController {

    private static final String PARENT_FORM_VIEW = "admin/parent-form";
    private static final String REDIRECT_PARENTS = "redirect:/parents";
    private static final String ERROR_PARENT_NOT_FOUND = "Representante no encontrado.";
    private static final String SUCCESS_PARENT_SAVED = "Representante guardado exitosamente.";
    private static final String SUCCESS_PARENT_DELETED = "Representante eliminado exitosamente.";
    private static final String ERROR_SAVING_PARENT = "Error al guardar el representante: ";
    private static final String ERROR_DELETING_PARENT = "Error al eliminar el representante: ";
    
    private final ParentService parentService;
    private final AcademicService academicService;

    public ParentController(ParentService parentService, AcademicService academicService) {
        this.parentService = parentService;
        this.academicService = academicService;
    }

    @GetMapping
    public String listParents(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        model.addAttribute("parents", parentService.getAllParents(pageable));
        return "admin/parent-list";
    }

    @GetMapping("/new")
    public String newParentForm(Model model) {
        model.addAttribute("parent", new Parent());
        model.addAttribute("students", academicService.getAllStudents());
        return PARENT_FORM_VIEW;
    }

    @PostMapping
    public String saveParent(@jakarta.validation.Valid @ModelAttribute @NonNull Parent parent,
            org.springframework.validation.BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("students", academicService.getAllStudents());
            return PARENT_FORM_VIEW;
        }
        try {
            // Hydrate children (Students) to ensure they are managed entities
            if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
                java.util.Set<com.school.academic.entity.Student> managedChildren = new java.util.HashSet<>();
                for (com.school.academic.entity.Student transientStudent : parent.getChildren()) {
                    if (transientStudent.getId() != null) {
                        academicService.getStudentById(java.util.Objects.requireNonNull(transientStudent.getId(),
                                "El ID del estudiante no puede ser null")).ifPresent(managedChildren::add);
                    }
                }
                parent.setChildren(managedChildren);
            }

            parentService.saveParent(parent);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_PARENT_SAVED);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("students", academicService.getAllStudents());
            return PARENT_FORM_VIEW;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_SAVING_PARENT + e.getMessage());
        }
        return REDIRECT_PARENTS;
    }

    @GetMapping("/edit/{id}")
    public String editParentForm(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        var parentOpt = parentService.getParentById(id);
        if (parentOpt.isPresent()) {
            model.addAttribute("parent", parentOpt.get());
            model.addAttribute("students", academicService.getAllStudents());
            return PARENT_FORM_VIEW;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_PARENT_NOT_FOUND);
            return REDIRECT_PARENTS;
        }
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteParent(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            parentService.deleteParent(id);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_PARENT_DELETED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_DELETING_PARENT + e.getMessage());
        }
        return REDIRECT_PARENTS;
    }
}