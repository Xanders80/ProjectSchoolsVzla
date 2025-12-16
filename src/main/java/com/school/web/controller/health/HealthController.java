package com.school.web.controller.health;

import com.school.academic.service.AcademicService;
import com.school.health.entity.MedicalRecord;
import com.school.health.entity.Vaccine;
import com.school.health.service.HealthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/health")
public class HealthController {

    private final HealthService healthService;
    private final AcademicService academicService;

    public HealthController(HealthService healthService, AcademicService academicService) {
        this.healthService = healthService;
        this.academicService = academicService;
    }

    @GetMapping("/student/{studentId}")
    public String viewHealthProfile(@PathVariable Long studentId, Model model) {
        model.addAttribute("student", academicService.getStudentById(studentId).orElseThrow());
        model.addAttribute("medicalRecord", healthService.getOrCreateMedicalRecord(studentId));
        model.addAttribute("vaccines", healthService.getVaccinesByStudentId(studentId));
        model.addAttribute("newVaccine", new Vaccine());
        return "health/profile";
    }

    @PostMapping("/student/{studentId}/record")
    public String updateMedicalRecord(@PathVariable Long studentId,
            @ModelAttribute("medicalRecord") MedicalRecord medicalRecord,
            RedirectAttributes redirectAttributes) {
        healthService.saveMedicalRecord(studentId, medicalRecord);
        redirectAttributes.addFlashAttribute("successMessage", "Ficha médica actualizada correctamente.");
        return "redirect:/health/student/" + studentId;
    }

    @PostMapping("/student/{studentId}/vaccine")
    public String addVaccine(@PathVariable Long studentId,
            @Valid @ModelAttribute("newVaccine") Vaccine vaccine,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar vacuna. Verifique los campos.");
            return "redirect:/health/student/" + studentId;
        }
        healthService.addVaccine(studentId, vaccine);
        redirectAttributes.addFlashAttribute("successMessage", "Vacuna registrada.");
        return "redirect:/health/student/" + studentId;
    }

    @RequestMapping(value = "/vaccine/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteVaccine(@PathVariable Long id, @RequestParam Long studentId,
            RedirectAttributes redirectAttributes) {
        healthService.deleteVaccine(id);
        redirectAttributes.addFlashAttribute("successMessage", "Vacuna eliminada.");
        return "redirect:/health/student/" + studentId;
    }
}
