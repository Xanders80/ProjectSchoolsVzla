package com.school.web.controller.academic;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.AcademicPeriod;
import com.school.academic.service.AcademicPeriodService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/academic-periods")
public class AcademicPeriodController {

	private final AcademicPeriodService periodService;

	public AcademicPeriodController(AcademicPeriodService periodService) {
		this.periodService = periodService;
	}

	@GetMapping
	public String listPeriods(Model model) {
		model.addAttribute("periods", periodService.findAll());
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
		periodService.save(period);
		redirectAttributes.addFlashAttribute("success", "Periodo academico guardado exitosamente");
		return "redirect:/academic-periods";
	}

	@GetMapping("/edit/{id}")
	public String editPeriodForm(@PathVariable @NonNull Long id, Model model) {
		AcademicPeriod period = periodService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("ID de periodo invalido: " + id));
		model.addAttribute("period", period);
		return "academic/period-form";
	}

	@PostMapping("/delete/{id}")
	public String deletePeriod(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
		periodService.deleteById(id);
		redirectAttributes.addFlashAttribute("success", "Periodo academico eliminado exitosamente");
		return "redirect:/academic-periods";
	}
}
