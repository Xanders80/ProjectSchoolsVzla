package com.school.web.controller.hr;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.admin.service.StaffService;
import com.school.hr.entity.Contract;
import com.school.hr.service.HRService;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
public class HRController {

    private static final String REDIRECT_CONTRACTS = "redirect:/hr/contracts";
    private static final String REDIRECT_ATTENDANCE = "redirect:/hr/attendance";
    private static final String SUCCESS_CONTRACT_SAVED = "Contrato guardado correctamente.";
    private static final String SUCCESS_CHECKIN = "Entrada registrada.";
    private static final String SUCCESS_CHECKOUT = "Salida registrada.";
    private static final String SUCCESS_PAYROLL_GENERATED = "Nómina generada para el período ";
    private static final String SUCCESS_PAYMENT_REGISTERED = "Pago registrado.";
    private static final String ERROR_START_DATE_REQUIRED = "Fecha de inicio es requerida";
    private static final String ERROR_STAFF_ID_NULL = "ID de personal no puede ser null";
    
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

        if (contract.getStartDate() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_START_DATE_REQUIRED);
            return REDIRECT_CONTRACTS;
        }

        if (contract.getStaff() != null && contract.getStaff().getId() != null) {
            staffService
                    .getStaffById(java.util.Objects.requireNonNull(contract.getStaff().getId(),
                            ERROR_STAFF_ID_NULL))
                    .ifPresent(contract::setStaff);
        }

        hrService.saveContract(contract);
        redirectAttributes.addFlashAttribute("successMessage", SUCCESS_CONTRACT_SAVED);
        return REDIRECT_CONTRACTS;
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
    public String manualCheckIn(@RequestParam @NonNull Long staffId,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            RedirectAttributes redirectAttributes) {
        hrService.markCheckIn(staffId, time);
        redirectAttributes.addFlashAttribute("successMessage", SUCCESS_CHECKIN);
        return REDIRECT_ATTENDANCE;
    }

    @PostMapping("/attendance/checkout")
    public String manualCheckOut(@RequestParam Long staffId,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            RedirectAttributes redirectAttributes) {
        try {
            hrService.markCheckOut(staffId, time);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_CHECKOUT);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return REDIRECT_ATTENDANCE;
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
        redirectAttributes.addFlashAttribute("successMessage", SUCCESS_PAYROLL_GENERATED + period);
        return "redirect:/hr/payroll?period=" + period;
    }

    @PostMapping("/payroll/pay/{id}")
    public String markAsPaid(@PathVariable @NonNull Long id, @RequestParam String period,
            RedirectAttributes redirectAttributes) {
        hrService.payPayroll(id);
        redirectAttributes.addFlashAttribute("successMessage", SUCCESS_PAYMENT_REGISTERED);
        return "redirect:/hr/payroll?period=" + period;
    }
}
