package com.school.web.controller.bi;

import com.school.bi.service.BiService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/bi")
@PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
public class BiController {

    private final BiService biService;

    public BiController(BiService biService) {
        this.biService = biService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("kpi", biService.getKpiStats());
        // We'll load charts via AJAX to separate concerns, or pass initial data.
        // Let's pass initial data for simple server-side rendering support if desired,
        // but AJAX is cleaner for charts.
        return "bi/dashboard";
    }

    @GetMapping("/api/financial-trend")
    @ResponseBody
    public Map<String, Object> getFinancialTrend() {
        return biService.getFinancialTrend();
    }

    @GetMapping("/api/academic-trend")
    @ResponseBody
    public Map<String, Object> getAcademicTrend() {
        return biService.getAcademicTrend();
    }
}
