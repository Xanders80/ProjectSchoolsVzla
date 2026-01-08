package com.school.web.controller.academic;

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

import com.school.academic.service.PromotionService;

@Controller
@RequestMapping("/academic/promotions")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/evaluate/{periodId}")
    public String evaluatePeriod(@PathVariable @NonNull Long periodId, Model model) {
        // En una implementación real, aquí mostraríamos la lista de estudiantes
        // y su estado de promoción calculado.
        model.addAttribute("periodId", periodId);
        return "academic/promotion-dashboard";
    }

    @PostMapping("/process/{periodId}")
    public String processMassPromotion(@PathVariable @NonNull Long periodId, RedirectAttributes redirectAttributes) {
        try {
            promotionService.processMassPromotion(periodId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Proceso de promoción masiva ejecutado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar promociones: " + e.getMessage());
        }
        return "redirect:/academic/promotions/evaluate/" + periodId;
    }

    @GetMapping("/student/{studentId}")
    public String evaluateStudent(@PathVariable @NonNull Long studentId, @RequestParam @NonNull Long periodId,
            Model model) {
        PromotionService.PromotionResult result = promotionService.evaluatePromotion(studentId, periodId);
        model.addAttribute("result", result);
        return "academic/student-promotion-result";
    }
}
