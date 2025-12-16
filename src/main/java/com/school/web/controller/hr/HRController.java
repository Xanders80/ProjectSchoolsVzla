package com.school.web.controller.hr;

import com.school.admin.service.StaffService;
import com.school.hr.entity.Contract;
import com.school.hr.entity.StaffAttendance;
import com.school.hr.service.HRService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
public class HRController {

    private final HRService hrService;
    private final StaffService staffService;

    public HRController(HRService hrService, StaffService staffService) {
        this.hrService = hrService;
        this.staffService = staffService;
    }

    // --- CONTRACTS ---

    @GetMapping("/contracts")
    public String viewContracts(Model model) {
        model.addAttribute("contracts", hrService.getAllContracts());
        model.addAttribute("staffList", staffService.getAllStaff()); // For dropdown
        model.addAttribute("newContract", new Contract());
        return "hr/contracts";
    }

    @PostMapping("/contracts/save")
    public String saveContract(@ModelAttribute Contract contract, RedirectAttributes redirectAttributes) {
        hrService.saveContract(contract);
        redirectAttributes.addFlashAttribute("successMessage", "Contrato guardado correctamente.");
        return "redirect:/hr/contracts";
    }

    // --- ATTENDANCE ---

    @GetMapping("/attendance")
    public String viewAttendance(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            Model model) {
        LocalDate viewDate = (date != null) ? date : LocalDate.now();
        model.addAttribute("date", viewDate);
        model.addAttribute("attendanceList", hrService.getAttendanceByDate(viewDate));
        model.addAttribute("staffList", staffService.getAllStaff()); // For manual check-in
        return "hr/attendance";
    }

    @PostMapping("/attendance/checkin")
    public String manualCheckIn(@RequestParam Long staffId,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            RedirectAttributes redirectAttributes) {
        hrService.markCheckIn(staffId, time);
        redirectAttributes.addFlashAttribute("successMessage", "Entrada registrada.");
        return "redirect:/hr/attendance";
    }

    @PostMapping("/attendance/checkout")
    public String manualCheckOut(@RequestParam Long staffId,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            RedirectAttributes redirectAttributes) {
        try {
            hrService.markCheckOut(staffId, time);
            redirectAttributes.addFlashAttribute("successMessage", "Salida registrada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/hr/attendance";
    }

    // --- PAYROLL ---

    @GetMapping("/payroll")
    public String viewPayroll(@RequestParam(required = false) String period, Model model) {
        String viewPeriod = (period != null && !period.isEmpty()) ? period : YearMonth.now().toString();
        model.addAttribute("period", viewPeriod);
        model.addAttribute("payrollList", hrService.getPayrollByPeriod(viewPeriod));
        return "hr/payroll";
    }

    @PostMapping("/payroll/generate")
    public String generatePayroll(@RequestParam String period, RedirectAttributes redirectAttributes) {
        hrService.generatePayrollForPeriod(period);
        redirectAttributes.addFlashAttribute("successMessage", "Nómina generada para el período " + period);
        return "redirect:/hr/payroll?period=" + period;
    }

    @PostMapping("/payroll/pay/{id}")
    public String markAsPaid(@PathVariable Long id, @RequestParam String period,
            RedirectAttributes redirectAttributes) {
        hrService.payPayroll(id);
        redirectAttributes.addFlashAttribute("successMessage", "Pago registrado.");
        return "redirect:/hr/payroll?period=" + period;
    }
}
