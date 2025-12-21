package com.school.web.controller.academic;

import com.school.academic.entity.AcademicPeriod;
import com.school.academic.repository.AcademicPeriodRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/academic-periods")
public class AcademicPeriodController {

    private final AcademicPeriodRepository periodRepository;

    public AcademicPeriodController(AcademicPeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }

    @GetMapping
    public String listPeriods(Model model) {
        model.addAttribute("periods", periodRepository.findAll());
        return "academic/period-list";
    }

    @GetMapping("/new")
    public String newPeriodForm(Model model) {
        model.addAttribute("period", new AcademicPeriod());
        return "academic/period-form";
    }

    @PostMapping
    public String savePeriod(@Valid @ModelAttribute("period") AcademicPeriod period,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "academic/period-form";
        }
        periodRepository.save(period);
        redirectAttributes.addFlashAttribute("success", "Periodo académico guardado exitosamente");
        return "redirect:/academic-periods";
    }

    @GetMapping("/edit/{id}")
    public String editPeriodForm(@PathVariable Long id, Model model) {
        AcademicPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de periodo inválido: " + id));
        model.addAttribute("period", period);
        return "academic/period-form";
    }

    @PostMapping("/delete/{id}")
    public String deletePeriod(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        periodRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Periodo académico eliminado exitosamente");
        return "redirect:/academic-periods";
    }
}
