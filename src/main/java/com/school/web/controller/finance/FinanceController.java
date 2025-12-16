package com.school.web.controller.finance;

import com.school.finance.entity.StudentFee;
import com.school.finance.service.FinanceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/fees")
    public String listFees(Model model) {
        model.addAttribute("fees", financeService.getAllFees());
        return "finance/fee-list";
    }

    @GetMapping("/fees/new")
    public String newFeeForm(Model model) {
        model.addAttribute("fee", new StudentFee());
        model.addAttribute("students", financeService.getAllStudents());
        return "finance/fee-form";
    }

    @PostMapping("/fees/save")
    public String saveFee(@ModelAttribute StudentFee fee, RedirectAttributes redirectAttributes) {
        financeService.createFee(fee);
        redirectAttributes.addFlashAttribute("success", "Cobro registrado exitosamente");
        return "redirect:/finance/fees";
    }

    @GetMapping("/payments/new")
    public String newPaymentForm(@RequestParam Long feeId, Model model) {
        StudentFee fee = financeService.getFeeById(feeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Fee ID"));

        com.school.finance.entity.Payment payment = new com.school.finance.entity.Payment();
        payment.setStudentFee(fee);

        model.addAttribute("payment", payment);
        model.addAttribute("fee", fee);
        model.addAttribute("paymentMethods", com.school.finance.enums.PaymentMethod.values());

        return "finance/payment-form";
    }

    @PostMapping("/payments/save")
    public String savePayment(@ModelAttribute com.school.finance.entity.Payment payment,
            RedirectAttributes redirectAttributes) {
        financeService.registerPayment(payment);
        redirectAttributes.addFlashAttribute("success", "Pago registrado exitosamente");
        return "redirect:/finance/fees";
    }
}
