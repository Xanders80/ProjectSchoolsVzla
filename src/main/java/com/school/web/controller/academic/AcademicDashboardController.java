package com.school.web.controller.academic;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.school.academic.service.AcademicDashboardService;

@Controller
@RequestMapping("/academic")
public class AcademicDashboardController {

    private final AcademicDashboardService dashboardService;

    public AcademicDashboardController(AcademicDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> metrics = dashboardService.getAcademicMetrics();
        
        model.addAttribute("totalStudents", metrics.get("totalStudents"));
        model.addAttribute("averageGrade", metrics.get("averageGrade"));
        model.addAttribute("attendanceRate", metrics.get("attendanceRate"));
        model.addAttribute("studentsWithAlerts", metrics.get("studentsWithAlerts"));
        
        return "academic/dashboard/academic-dashboard";
    }
}