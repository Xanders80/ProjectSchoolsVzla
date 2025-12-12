package com.school.web.controller.portal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portal")
public class PortalController {

    @GetMapping("/student")
    public String studentDashboard(Model model) {
        // Mock data for prototype
        model.addAttribute("title", "Student Portal");
        return "portal/student-dashboard";
    }

    @GetMapping("/parent")
    public String parentDashboard(Model model) {
        // Mock data
        model.addAttribute("title", "Parent Portal");
        return "portal/parent-dashboard";
    }

    @GetMapping("/grades")
    public String viewGrades(Model model) {
        return "portal/grades-view";
    }
}
