package com.school.web.controller.academic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.Student;
import com.school.academic.service.AcademicService;
import com.school.core.validation.ValidId;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/students")
@Validated
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private static final String STUDENT_FORM_VIEW = "academic/student-form";
    private static final String REDIRECT_STUDENTS = "redirect:/students";
    private static final String MSG_SUCCESS = "successMessage";
    private static final String MSG_ERROR = "errorMessage";
    private static final String STR_ERROR = "error";

    private final AcademicService academicService;

    public StudentController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping
    public String listStudents(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Student> studentPage = academicService.getAllStudents(pageable);
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
            @Valid @ModelAttribute Student student,
            org.springframework.validation.BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            logger.warn("Validation errors while saving student: {}", result.getAllErrors());
            return STUDENT_FORM_VIEW;
        }
        try {
            Student savedStudent = academicService
                    .saveStudent(java.util.Objects.requireNonNull(student, "El estudiante no puede ser null"));
            redirectAttributes.addFlashAttribute(MSG_SUCCESS,
                    "Estudiante guardado exitosamente. Número de registro: " + savedStudent.getRegistrationNumber());
            return REDIRECT_STUDENTS;
        } catch (IllegalArgumentException e) {
            logger.warn("Business rule violation saving student: {}", e.getMessage());
            model.addAttribute(MSG_ERROR, e.getMessage());
            return STUDENT_FORM_VIEW;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Data integrity violation saving student", e);
            model.addAttribute(MSG_ERROR,
                    "Error de integridad de datos. Es posible que el DNI o número de registro ya existan.");
            return STUDENT_FORM_VIEW;
        } catch (Exception e) {
            logger.error("Unexpected error saving student", e);
            model.addAttribute(MSG_ERROR, "Ocurrió un error inesperado al guardar el estudiante.");
            return STUDENT_FORM_VIEW;
        }
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable @NonNull Long id, Model model) {
        Student student = academicService.getStudentById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        model.addAttribute("student", student);
        return STUDENT_FORM_VIEW;
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public String deleteStudent(@PathVariable @ValidId String id,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Long studentId;

        try {
            studentId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            logger.warn("Invalid student ID format: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute(MSG_ERROR, "ID de estudiante inválido");
            return REDIRECT_STUDENTS;
        }

        try {
            academicService.deleteStudent(studentId);
            logger.info("Student {} deleted successfully by IP: {}", studentId, clientIp);
            redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Estudiante eliminado exitosamente");
        } catch (EntityNotFoundException e) {
            logger.warn("Attempt to delete non-existent student ID: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute(MSG_ERROR, "Estudiante no encontrado");
        } catch (IllegalStateException e) {
            logger.warn("Business rule violation deleting student ID: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute(MSG_ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting student ID: {} from IP: {}", id, clientIp, e);
            redirectAttributes.addFlashAttribute(MSG_ERROR, "Error interno del sistema");
        }
        return REDIRECT_STUDENTS;
    }

    @DeleteMapping("/api/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteStudentApi(@PathVariable @ValidId String id,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Long studentId;

        try {
            studentId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            logger.warn("Invalid student ID format in API call: {} from IP: {}", id, clientIp);
            return ResponseEntity.badRequest().body(java.util.Map.of(STR_ERROR, "ID de estudiante inválido"));
        }

        try {
            academicService.deleteStudent(studentId);
            logger.info("Student {} deleted via API by IP: {}", studentId, clientIp);
            return ResponseEntity.ok(java.util.Map.of("message", "Student deleted successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("API call to delete non-existent student ID: {} from IP: {}", id, clientIp);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of(STR_ERROR, "Student not found"));
        } catch (Exception e) {
            logger.error("API delete error for student ID: {} from IP: {}", id, clientIp, e);
            return ResponseEntity.status(500).body(java.util.Map.of(STR_ERROR, "Error deleting student"));
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException ex, Model model) {
        model.addAttribute(MSG_ERROR, ex.getMessage());
        return STR_ERROR; // Asegúrate de que esta vista existe
    }
}