package com.school.web.controller.schedule;

import com.school.academic.repository.SectionRepository;
import com.school.schedule.entity.ScheduleEntry;
import com.school.schedule.service.ScheduleService;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final SectionRepository sectionRepository;

    public ScheduleController(ScheduleService scheduleService, SectionRepository sectionRepository) {
        this.scheduleService = scheduleService;
        this.sectionRepository = sectionRepository;
    }

    @GetMapping
    public String listSchedules(Model model) {
        model.addAttribute("scheduleEntries", scheduleService.getAllSchedules());
        return "schedule/schedule-list";
    }

    @GetMapping("/new")
    public String newScheduleForm(Model model) {
        model.addAttribute("scheduleEntry", new ScheduleEntry());
        model.addAttribute("sections", sectionRepository.findAll());
        model.addAttribute("days", DayOfWeek.values());
        return "schedule/schedule-form";
    }

    @PostMapping
    public String saveSchedule(@ModelAttribute ScheduleEntry scheduleEntry, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.saveSchedule(scheduleEntry);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/schedules/new";
        }
        return "redirect:/schedules";
    }

    @GetMapping("/edit/{id}")
    public String editScheduleForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("scheduleEntry", scheduleService.getScheduleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule Id:" + id)));
        model.addAttribute("sections", sectionRepository.findAll());
        model.addAttribute("days", DayOfWeek.values());
        return "schedule/schedule-form";
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteSchedule(@PathVariable @NonNull Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/schedules";
    }
}
