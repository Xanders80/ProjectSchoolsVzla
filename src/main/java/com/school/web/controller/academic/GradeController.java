package com.school.web.controller.academic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.Grade;
import com.school.academic.enums.EvaluationType;
import com.school.academic.service.AcademicPeriodService;
import com.school.academic.service.AcademicService;

@Controller
@RequestMapping("/grades")
public class GradeController {

	private static final Logger logger = LoggerFactory.getLogger(GradeController.class);

	private static final String MSG_SUCCESS = "successMessage";
	private static final String MSG_ERROR = "errorMessage";

	private final AcademicService academicService;
	private final AcademicPeriodService academicPeriodService;

	public GradeController(AcademicService academicService,
			AcademicPeriodService academicPeriodService) {
		this.academicService = academicService;
		this.academicPeriodService = academicPeriodService;
	}

	@GetMapping
	public String listGrades(Model model) {
		java.util.List<Grade> grades = academicService.getAllGrades();
		logger.info("Retrieved {} grades from database", grades.size());
		model.addAttribute("grades", grades);
		return "academic/grade-list";
	}

	@GetMapping("/new")
	public String newGradeForm(Model model) {
		model.addAttribute("grade", new Grade());
		populateDropdowns(model);
		return "academic/grade-form";
	}

	@PostMapping
	public String saveGrade(@ModelAttribute Grade grade, RedirectAttributes redirectAttributes) {
		if (grade == null) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, "El objeto de calificación no puede ser nulo");
			return "redirect:/grades";
		}
		try {
			academicService.saveGrade(grade);
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Calificación guardada exitosamente");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, e.getMessage());
		}
		return "redirect:/grades";
	}

	@GetMapping("/edit/{id}")
	public String editGradeForm(@PathVariable @NonNull Long id, Model model) {
		model.addAttribute("grade", academicService.getGradeById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid grade Id:" + id)));
		populateDropdowns(model);
		return "academic/grade-form";
	}

	@RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
	public String deleteGrade(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
		try {
			academicService.deleteGrade(id);
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Calificación eliminada exitosamente");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, e.getMessage());
		}
		return "redirect:/grades";
	}

	private void populateDropdowns(Model model) {
		model.addAttribute("students",
				academicService.getAllStudents(org.springframework.data.domain.Pageable.unpaged()).getContent());
		model.addAttribute("courses", academicService.getAllCourses());
		model.addAttribute("periods", academicPeriodService.findAll());
		model.addAttribute("evaluationTypes", EvaluationType.values());
	}
}
