package com.school.web.controller.schedule;

import java.time.DayOfWeek;

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

import jakarta.validation.Valid;

import com.school.academic.service.SectionService;
import com.school.academic.service.BatchScheduleService;
import com.school.schedule.entity.ScheduleEntry;
import com.school.schedule.service.ScheduleService;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

	private static final String MSG_SUCCESS = "successMessage";
	private static final String MSG_ERROR = "errorMessage";

	private final ScheduleService scheduleService;
	private final SectionService sectionService;
	private final BatchScheduleService batchService;

	public ScheduleController(ScheduleService scheduleService,
			SectionService sectionService,
			BatchScheduleService batchService) {
		this.scheduleService = scheduleService;
		this.sectionService = sectionService;
		this.batchService = batchService;
	}

	@GetMapping
	public String listSchedules(Model model) {
		model.addAttribute("scheduleEntries", scheduleService.getAllSchedules());
		return "schedule/schedule-list";
	}

	@GetMapping("/new")
	public String newScheduleForm(Model model) {
		model.addAttribute("scheduleEntry", new ScheduleEntry());
		model.addAttribute("sections",
				sectionService.getAllActiveSections(org.springframework.data.domain.Pageable.unpaged()).getContent());
		model.addAttribute("days", DayOfWeek.values());
		return "schedule/schedule-form";
	}

	@PostMapping
	public String saveSchedule(@Valid @ModelAttribute ScheduleEntry scheduleEntry,
			org.springframework.validation.BindingResult result,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (result.hasErrors()) {
			model.addAttribute("sections",
					sectionService.getAllActiveSections(org.springframework.data.domain.Pageable.unpaged())
							.getContent());
			model.addAttribute("days", DayOfWeek.values());
			return "schedule/schedule-form";
		}

		try {
			if (scheduleEntry.getSection() != null && scheduleEntry.getSection().getId() != null) {
				sectionService.findById(scheduleEntry.getSection().getId())
						.ifPresent(scheduleEntry::setSection);
			}

			scheduleService.saveSchedule(scheduleEntry);
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Horario guardado exitosamente");
		} catch (IllegalArgumentException | IllegalStateException e) {
			model.addAttribute(MSG_ERROR, e.getMessage());
			model.addAttribute("sections",
					sectionService.getAllActiveSections(org.springframework.data.domain.Pageable.unpaged())
							.getContent());
			model.addAttribute("days", DayOfWeek.values());
			return "schedule/schedule-form";
		}
		return "redirect:/schedules";
	}

	@GetMapping("/edit/{id}")
	public String editScheduleForm(@PathVariable @NonNull Long id, Model model) {
		model.addAttribute("scheduleEntry", scheduleService.getScheduleById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid schedule Id:" + id)));
		model.addAttribute("sections",
				sectionService.getAllActiveSections(org.springframework.data.domain.Pageable.unpaged()).getContent());
		model.addAttribute("days", DayOfWeek.values());
		return "schedule/schedule-form";
	}

	@RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
	public String deleteSchedule(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
		try {
			scheduleService.deleteSchedule(id);
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Horario eliminado exitosamente");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, e.getMessage());
		}
		return "redirect:/schedules";
	}

	@PostMapping("/batch/start")
	public String startBatchProcess(RedirectAttributes redirectAttributes) {
		try {
			var entries = scheduleService.getAllSchedules();
			batchService.processBulkSchedules(entries);
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Proceso masivo de horarios iniciado en segundo plano");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, "Error al iniciar proceso masivo: " + e.getMessage());
		}
		return "redirect:/schedules";
	}
}
