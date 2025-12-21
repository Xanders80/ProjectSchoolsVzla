package com.school.web.controller.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.school.report.dto.ChartDataDTO;
import com.school.report.service.ReportingService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/reports")
public class AnalyticsController {

    private final ReportingService reportingService;

    public AnalyticsController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        ChartDataDTO enrollmentData = reportingService.getStudentEnrollmentData();
        ChartDataDTO feeData = reportingService.getFeeStatusData();
        Map<String, BigDecimal> financeSummary = reportingService.getFinancialSummary();

        model.addAttribute("enrollmentLabels", enrollmentData.getLabels());
        model.addAttribute("enrollmentData", enrollmentData.getData());

        model.addAttribute("feeLabels", feeData.getLabels());
        model.addAttribute("feeData", feeData.getData());

        model.addAttribute("financeSummary", financeSummary);

        return "reports/dashboard";
    }

    @GetMapping("/export/fees")
    public void exportFees(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"finance_report.csv\"");

        // Simple CSV generation
        // In a real app, use a library like OpenCSV or Apache POI
        // Keeping it dependency-free for this MVP
        StringBuilder csv = new StringBuilder();
        csv.append("Type,Amount\n");

        Map<String, BigDecimal> summary = reportingService.getFinancialSummary();
        csv.append("Collected,").append(summary.get("collected")).append("\n");
        csv.append("Pending,").append(summary.get("pending")).append("\n");
        csv.append("Partial,").append(summary.getOrDefault("partial", BigDecimal.ZERO)).append("\n"); // If logic added
                                                                                                      // later

        response.getWriter().write(csv.toString());
    }
}
