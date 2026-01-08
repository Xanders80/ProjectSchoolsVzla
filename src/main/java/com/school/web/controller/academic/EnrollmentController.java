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

    @PostMapping("/delete")
    public String unenrollStudent(@RequestParam @NonNull Long enrollmentId, @RequestParam @NonNull Long sectionId,
            RedirectAttributes redirectAttributes) {
        try {
            academicService.unenrollStudent(enrollmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Inscripción eliminada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar inscripción: " + e.getMessage());
        }
        return "redirect:/enrollments/section/" + sectionId;
    }

    @GetMapping("/transfer/{studentId}")
    public String showTransferForm(@PathVariable @NonNull Long studentId, @RequestParam @NonNull Long currentSectionId,
            Model model) {
        Student student = academicService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + studentId));
        List<Section> availableSections = academicService.getAllSections().stream()
                .filter(s -> !s.getId().equals(currentSectionId))
                .toList();

        model.addAttribute("student", student);
        model.addAttribute("currentSectionId", currentSectionId);
        model.addAttribute("sections", availableSections);
        return "academic/enrollment-transfer";
    }

    @PostMapping("/transfer")
    public String transferStudent(@RequestParam @NonNull Long studentId, @RequestParam @NonNull Long fromSectionId,
            @RequestParam @NonNull Long toSectionId, RedirectAttributes redirectAttributes) {
        try {
            academicService.transferStudent(studentId, fromSectionId, toSectionId);
            redirectAttributes.addFlashAttribute("successMessage", "Estudiante transferido exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al transferir: " + e.getMessage());
        }
        return "redirect:/enrollments/section/" + toSectionId;
    }

    @PostMapping("/batch-reenroll")
    public String batchReenroll(@RequestParam @NonNull Long sectionId, @RequestParam @NonNull List<Long> studentIds,
            RedirectAttributes redirectAttributes) {
        try {
            academicService.batchReenroll(sectionId, studentIds);
            redirectAttributes.addFlashAttribute("successMessage", "Reinscripción masiva completada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error en reinscripción: " + e.getMessage());
        }
        return "redirect:/enrollments/section/" + sectionId;
    }
}
