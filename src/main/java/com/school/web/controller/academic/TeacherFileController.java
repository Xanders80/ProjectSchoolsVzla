package com.school.web.controller.academic;

import com.school.academic.entity.TeacherDevelopment;
import com.school.academic.service.TeacherManagementService;
import com.school.core.service.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/academic/teachers/files")
public class TeacherFileController {

    private final TeacherManagementService managementService;
    private final StorageService storageService;

    public TeacherFileController(TeacherManagementService managementService, StorageService storageService) {
        this.managementService = managementService;
        this.storageService = storageService;
    }

    @PostMapping("/profile/{profileId}/create")
    public String createLogWithFile(@PathVariable Long profileId,
            @RequestParam("title") String title,
            @RequestParam("institution") String institution,
            @RequestParam("attainmentDate") String attainmentDate,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            String filename = storageService.store(file);

            TeacherDevelopment dev = managementService.createProfessionalLogWithFile(
                    profileId,
                    title,
                    institution,
                    java.time.LocalDate.parse(attainmentDate),
                    filename);

            Long staffId = dev.getTeacherProfile().getStaff().getId();
            redirectAttributes.addFlashAttribute("successMessage", "Documento guardado exitosamente en el expediente");
            return "redirect:/academic/teachers/" + staffId + "/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar documento: " + e.getMessage());
            return "redirect:/academic/teachers"; // Fallback
        }
    }
}
