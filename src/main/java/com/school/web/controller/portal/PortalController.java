package com.school.web.controller.portal;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.Student;
import com.school.academic.service.AcademicService;
import com.school.core.entity.Parent;
import com.school.core.service.ParentService;
import com.school.core.service.UserService;
import com.school.finance.service.FinanceService;

@Controller
@RequestMapping("/portal")
public class PortalController {

    private final ParentService parentService;
    private final UserService userService;
    private final AcademicService academicService;
    private final FinanceService financeService;

    public PortalController(ParentService parentService, UserService userService, AcademicService academicService,
            FinanceService financeService) {
        this.parentService = parentService;
        this.userService = userService;
        this.academicService = academicService;
        this.financeService = financeService;
    }

    @GetMapping
    public String dashboard(Model model) {
        Parent parent = getLoggedParent();
        if (parent == null) {
            return "redirect:/login"; // Should be handled by security, but extra safety
        }

        Long parentId = parent.getId();
        if (parentId == null) {
            throw new IllegalStateException("Parent entity has null ID - invalid state");
        }

        Parent parentWithChildren = parentService.getParentById(parentId).orElse(parent);

        model.addAttribute("parent", parentWithChildren);
        model.addAttribute("children", parentWithChildren.getChildren());
        return "portal/dashboard";
    }

    @GetMapping("/student/{id}")
    public String studentDetail(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        Parent parent = getLoggedParent();
        if (parent == null)
            return "redirect:/login";

        // Verificación de nulabilidad para parent.getId()
        Long parentId = parent.getId();
        if (parentId == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error de acceso: El registro del padre no contiene un ID válido.");
            return "redirect:/portal";
        }

        // Verify access: Is this student a child of the logged parent?
        boolean isChild = parentService.getParentById(parentId)
                .map(p -> p.getChildren().stream().anyMatch(s -> s.getId().equals(id)))
                .orElse(false);

        if (!isChild) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Acceso denegado: Este estudiante no está asociado a su cuenta.");
            return "redirect:/portal";
        }

        Student student = academicService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID"));

        model.addAttribute("student", student);
        model.addAttribute("grades", academicService.getGradesByStudent(id));
        model.addAttribute("fees", financeService.getFeesByStudent(id));
        // Attendance not yet added to AcademicService - will add
        model.addAttribute("attendance", academicService.getAttendanceByStudent(id));

        return "portal/student-detail";
    }

    private Parent getLoggedParent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            return null;

        String username = auth.getName();
        return userService.findByUsername(username)
                .flatMap(user -> parentService.getParentByUserId(user.getId()))
                .orElse(null);
    }
}
