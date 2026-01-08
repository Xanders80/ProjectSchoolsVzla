package com.school.web.controller.infra;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.school.infra.dto.LabStatisticsDTO;
import com.school.infra.dto.TeacherUsageDTO;
import com.school.infra.service.LabStatisticsService;
import com.school.infra.service.ReportExportService;

@Controller
@RequestMapping("/infra/labs/statistics")
public class LabStatisticsController {

    private final LabStatisticsService statisticsService;
    private final ReportExportService exportService;

    public LabStatisticsController(LabStatisticsService statisticsService, ReportExportService exportService) {
        this.statisticsService = statisticsService;
        this.exportService = exportService;
    }

    @GetMapping
    public String showStatistics(Model model) {
        // Default: last 30 days
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(30);

        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("generalStats", statisticsService.getGeneralStatistics(from, to));

        return "infra/lab-statistics";
    }

    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getStatisticsData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from == null) {
            from = LocalDate.now().minusDays(30);
        }
        if (to == null) {
            to = LocalDate.now();
        }

        List<LabStatisticsDTO> labStats = statisticsService.getStatisticsByDateRange(from, to);
        List<TeacherUsageDTO> topTeachers = statisticsService.getTopTeachersByUsage(10, from, to);
        Map<String, Long> statusDistribution = statisticsService.getReservationsByStatus(from, to);
        Map<Integer, Long> peakHours = statisticsService.getPeakHoursStatistics(from, to);
        Map<String, Object> generalStats = statisticsService.getGeneralStatistics(from, to);

        return Map.of(
                "labStatistics", labStats,
                "topTeachers", topTeachers,
                "statusDistribution", statusDistribution,
                "peakHours", peakHours,
                "generalStats", generalStats);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportToPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from == null) {
            from = LocalDate.now().minusDays(30);
        }
        if (to == null) {
            to = LocalDate.now();
        }

        try {
            byte[] pdfBytes = exportService.exportToPdf(from, to);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "reporte_laboratorios_" + LocalDate.now() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from == null) {
            from = LocalDate.now().minusDays(30);
        }
        if (to == null) {
            to = LocalDate.now();
        }

        try {
            byte[] excelBytes = exportService.exportToExcel(from, to);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment",
                    "reporte_laboratorios_" + LocalDate.now() + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
