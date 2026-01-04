package com.school.web.controller.academic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.Student;
import com.school.academic.service.AcademicService;
import com.school.core.validation.ValidId;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/students")
@Validated
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private static final String STUDENT_FORM_VIEW = "academic/student-form";
    private final AcademicService academicService;

    public StudentController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping
    public String listStudents(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        org.springframework.data.domain.Page<Student> studentPage = academicService.getAllStudents(pageable);
        model.addAttribute("students", studentPage);
        return "academic/student-list";
    }

    @GetMapping("/new")
    public String newStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return STUDENT_FORM_VIEW;
    }

    @PostMapping
    public String saveStudent(
            @org.springframework.validation.annotation.Validated({
                    com.school.academic.validation.ValidationGroups.Create.class,
                    jakarta.validation.groups.Default.class }) @ModelAttribute @NonNull Student student,
            org.springframework.validation.BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return STUDENT_FORM_VIEW;
        }
        try {
            Student savedStudent = academicService.saveStudent(student);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Estudiante guardado exitosamente. Número de registro: " + savedStudent.getRegistrationNumber());
            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return STUDENT_FORM_VIEW;
        }
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable @NonNull Long id, Model model) {
        AcademicService service = this.academicService;
        // Note: Using lambda or Optional check wrapper is better practice, direct get()
        // is risky but acceptable for prototype
        model.addAttribute("student",
                service.getStudentById(id).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id)));
        return STUDENT_FORM_VIEW;
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public String deleteStudent(@PathVariable @ValidId String id,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();

        try {
            Long studentId = Long.parseLong(id);
            academicService.deleteStudent(studentId);
            logger.info("Student {} deleted successfully by IP: {}", studentId, clientIp);
            redirectAttributes.addFlashAttribute("successMessage", "Estudiante eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            logger.warn("Attempt to delete non-existent student ID: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute("errorMessage", "Estudiante no encontrado");
        } catch (IllegalStateException e) {
            logger.warn("Business rule violation deleting student ID: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting student ID: {} from IP: {}", id, clientIp, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del sistema");
        }
        return "redirect:/students";
    }

    @DeleteMapping("/api/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> deleteStudentApi(@PathVariable @ValidId String id,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();

        try {
            Long studentId = Long.parseLong(id);
            academicService.deleteStudent(studentId);
            logger.info("Student {} deleted via API by IP: {}", studentId, clientIp);
            return org.springframework.http.ResponseEntity
                    .ok(java.util.Map.of("message", "Student deleted successfully"));
        } catch (Exception e) {
            logger.error("API delete error for student ID: {} from IP: {}", id, clientIp, e);
            return org.springframework.http.ResponseEntity.status(500)
                    .body(java.util.Map.of("error", "Error deleting student"));
        }
    }
}
