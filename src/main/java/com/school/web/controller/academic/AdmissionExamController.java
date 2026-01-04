package com.school.web.controller.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.AdmissionExam;
import com.school.academic.service.AdmissionExamService;

@Controller
@RequestMapping("/academic/admissions")
public class AdmissionExamController {

    private static final String FORM_VIEW = "academic/admission-form";
    private static final String LIST_VIEW = "academic/admission-list";

    private final AdmissionExamService admissionExamService;

    public AdmissionExamController(AdmissionExamService admissionExamService) {
        this.admissionExamService = admissionExamService;
    }

    @GetMapping
    public String listExams(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "examDate"));
        Page<AdmissionExam> exams = admissionExamService.getAllExams(pageable);
        model.addAttribute("exams", exams);
        return LIST_VIEW;
    }

    @GetMapping("/new")
    public String newExamForm(Model model) {
        model.addAttribute("exam", new AdmissionExam());
        return FORM_VIEW;
    }

    @PostMapping
    public String saveExam(@jakarta.validation.Valid @ModelAttribute("exam") @NonNull AdmissionExam exam,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            admissionExamService.saveExam(exam);
            redirectAttributes.addFlashAttribute("successMessage", "Examen de admisión registrado exitosamente");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al guardar el examen: " + e.getMessage());
            return FORM_VIEW;
        }
        return "redirect:/academic/admissions";
    }

    @GetMapping("/edit/{id}")
    public String editExamForm(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        return admissionExamService.getExamById(id)
                .map(exam -> {
                    model.addAttribute("exam", exam);
                    return FORM_VIEW;
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Examen no encontrado");
                    return "redirect:/academic/admissions";
                });
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteExam(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            admissionExamService.deleteExam(id);
            redirectAttributes.addFlashAttribute("successMessage", "Examen eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el examen.");
        }
        return "redirect:/academic/admissions";
    }

    @PostMapping("/approve/{id}")
    public String approveExam(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            admissionExamService.approveExam(id);
            redirectAttributes.addFlashAttribute("successMessage", "Postulante aprobado exitosamente");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al aprobar el postulante");
        }
        return "redirect:/academic/admissions";
    }

    @PostMapping("/reject/{id}")
    public String rejectExam(@PathVariable @NonNull Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        try {
            admissionExamService.rejectExam(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Postulante rechazado");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al rechazar el postulante");
        }
        return "redirect:/academic/admissions";
    }

    @PostMapping("/enroll/{id}")
    public String enrollApplicant(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            com.school.academic.entity.Student student = admissionExamService.enrollApplicant(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Postulante inscrito exitosamente. Matrícula: " + student.getRegistrationNumber() + 
                    ". Se ha generado una contraseña temporal segura.");
            redirectAttributes.addFlashAttribute("showPasswordInfo", true);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al inscribir el postulante: " + e.getMessage());
        }
        return "redirect:/academic/admissions";
    }
}
