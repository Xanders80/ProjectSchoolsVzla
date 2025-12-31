package com.school.web.controller.academic;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
@RequestMapping("/students")
public class StudentController {

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
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            return STUDENT_FORM_VIEW;
        }
        try {
            academicService.saveStudent(student);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return STUDENT_FORM_VIEW;
        }
        return "redirect:/students";
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
    public String deleteStudent(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            academicService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Estudiante eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Estudiante no encontrado");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del sistema");
        }
        return "redirect:/students";
    }

    @DeleteMapping("/api/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> deleteStudentApi(@PathVariable @NonNull Long id) {
        try {
            academicService.deleteStudent(id);
            return org.springframework.http.ResponseEntity
                    .ok(java.util.Map.of("message", "Student deleted successfully"));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(500)
                    .body(java.util.Map.of("error", "Error deleting student"));
        }
    }
}
