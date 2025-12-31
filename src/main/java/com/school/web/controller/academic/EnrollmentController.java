package com.school.web.controller.academic;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.Enrollment;
import com.school.academic.entity.Section;
import com.school.academic.entity.Student;
import com.school.academic.service.AcademicService;

@Controller
@RequestMapping("/enrollments")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class EnrollmentController {

    private final AcademicService academicService;

    public EnrollmentController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping("/section/{sectionId}")
    public String listEnrollments(@PathVariable @NonNull Long sectionId, Model model) {
        Section section = academicService.getSectionById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + sectionId));

        List<Enrollment> enrollments = academicService.getEnrollmentsBySection(sectionId);

        model.addAttribute("section", section);
        model.addAttribute("enrollments", enrollments);
        return "academic/enrollment-list";
    }

    @GetMapping("/section/{sectionId}/new")
    public String newEnrollmentForm(@PathVariable @NonNull Long sectionId, Model model) {
        Section section = academicService.getSectionById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + sectionId));

        List<Student> availableStudents = academicService.getStudentsNotInSection(sectionId);

        model.addAttribute("section", section);
        model.addAttribute("students", availableStudents);
        return "academic/enrollment-form";
    }

    @PostMapping
    public String enrollStudent(@RequestParam @NonNull Long studentId, @RequestParam @NonNull Long sectionId,
            RedirectAttributes redirectAttributes) {
        try {
            academicService.enrollStudent(studentId, sectionId);
            redirectAttributes.addFlashAttribute("successMessage", "Estudiante inscrito exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al inscribir: " + e.getMessage());
        }
        return "redirect:/enrollments/section/" + sectionId;
    }

    @PostMapping("/delete/{id}")
    public String unenrollStudent(@PathVariable @NonNull Long id, @RequestParam @NonNull Long sectionId,
            RedirectAttributes redirectAttributes) {
        try {
            academicService.unenrollStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Inscripción eliminada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar inscripción: " + e.getMessage());
        }
        return "redirect:/enrollments/section/" + sectionId;
    }
}
