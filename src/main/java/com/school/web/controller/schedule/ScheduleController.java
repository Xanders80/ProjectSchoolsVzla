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

import com.school.academic.repository.SectionRepository;
import com.school.academic.service.BatchScheduleService;
import com.school.schedule.entity.ScheduleEntry;
import com.school.schedule.service.ScheduleService;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final SectionRepository sectionRepository;
    private final BatchScheduleService batchService;

    public ScheduleController(ScheduleService scheduleService,
            SectionRepository sectionRepository,
            BatchScheduleService batchService) {
        this.scheduleService = scheduleService;
        this.sectionRepository = sectionRepository;
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
                sectionRepository.findByDeletedFalse(org.springframework.data.domain.Pageable.unpaged()).getContent());
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
                    sectionRepository.findByDeletedFalse(org.springframework.data.domain.Pageable.unpaged())
                            .getContent());
            model.addAttribute("days", DayOfWeek.values());
            return "schedule/schedule-form";
        }

        try {
            scheduleService.saveSchedule(scheduleEntry);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("sections",
                    sectionRepository.findByDeletedFalse(org.springframework.data.domain.Pageable.unpaged())
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
                sectionRepository.findByDeletedFalse(org.springframework.data.domain.Pageable.unpaged()).getContent());
        model.addAttribute("days", DayOfWeek.values());
        return "schedule/schedule-form";
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteSchedule(@PathVariable @NonNull Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/schedules";
    }

    @PostMapping("/batch/start")
    public String startBatchProcess(RedirectAttributes redirectAttributes) {
        // Obtenemos todos los horarios actuales para re-procesar/validar masivamente
        var entries = scheduleService.getAllSchedules();
        batchService.processBulkSchedules(entries);

        redirectAttributes.addFlashAttribute("successMessage", "Proceso masivo de horarios iniciado en segundo plano");
        return "redirect:/schedules";
    }
}
