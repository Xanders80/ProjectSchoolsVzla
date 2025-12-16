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
    public String viewHealthProfile(@PathVariable @org.springframework.lang.NonNull Long studentId, Model model) {
        model.addAttribute("student", 
                academicService.getStudentById(studentId).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + studentId)));
        model.addAttribute("medicalRecord", healthService.getOrCreateMedicalRecord(studentId));
        model.addAttribute("vaccines", healthService.getVaccinesByStudentId(studentId));
        model.addAttribute("newVaccine", new Vaccine());
        return "health/profile";
    }

    @PostMapping("/student/{studentId}/record")
    public String updateMedicalRecord(@PathVariable @org.springframework.lang.NonNull Long studentId,
            @Valid @ModelAttribute("medicalRecord") @org.springframework.lang.NonNull MedicalRecord medicalRecord,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("student", 
                    academicService.getStudentById(studentId).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + studentId)));
            model.addAttribute("vaccines", healthService.getVaccinesByStudentId(studentId));
            model.addAttribute("newVaccine", new Vaccine());
            return "health/profile";
        }
        healthService.saveMedicalRecord(studentId, medicalRecord);
        return "redirect:/health/student/" + studentId;
    }

    @PostMapping("/student/{studentId}/vaccine")
    public String addVaccine(@PathVariable @org.springframework.lang.NonNull Long studentId,
            @Valid @ModelAttribute("newVaccine") @org.springframework.lang.NonNull Vaccine vaccine,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("student", 
                    academicService.getStudentById(studentId).orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + studentId)));
            model.addAttribute("medicalRecord", healthService.getOrCreateMedicalRecord(studentId));
            model.addAttribute("vaccines", healthService.getVaccinesByStudentId(studentId));
            return "health/profile";
        }
        healthService.addVaccine(studentId, vaccine);
        return "redirect:/health/student/" + studentId;
    }

    @RequestMapping(value = "/vaccine/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteVaccine(@PathVariable @org.springframework.lang.NonNull Long id, 
            @RequestParam @org.springframework.lang.NonNull Long studentId) {
        healthService.deleteVaccine(id);
        return "redirect:/health/student/" + studentId;
    }
}
