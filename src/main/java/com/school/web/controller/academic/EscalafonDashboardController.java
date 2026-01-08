package com.school.web.controller.academic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.school.academic.service.TeacherManagementService;

@Controller
@RequestMapping("/academic/escalafon")
public class EscalafonDashboardController {

    private final TeacherManagementService managementService;

    public EscalafonDashboardController(TeacherManagementService managementService) {
        this.managementService = managementService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", managementService.getEscalafonStats());
        return "academic/escalafon-dashboard";
    }
}
