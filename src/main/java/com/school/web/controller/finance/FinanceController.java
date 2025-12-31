package com.school.web.controller.finance;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.school.finance.entity.StudentFee;
import com.school.finance.service.FinanceService;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private static final String FEE_FORM_VIEW = "finance/fee-form";
    private static final String PAYMENT_FORM_VIEW = "finance/payment-form";
    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/fees")
    public String listFees(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        org.springframework.data.domain.Page<StudentFee> feePage = financeService.getAllFees(pageable);
        model.addAttribute("fees", feePage);
        return "finance/fee-list";
    }

    @GetMapping("/fees/new")
    public String newFeeForm(Model model) {
        model.addAttribute("fee", new StudentFee());
        model.addAttribute("students", financeService.getAllStudents());
        return FEE_FORM_VIEW;
    }

    @PostMapping("/fees")
    public String saveFee(@jakarta.validation.Valid @ModelAttribute @NonNull StudentFee fee,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("students", financeService.getAllStudents());
            return FEE_FORM_VIEW;
        }
        financeService.createFee(fee);
        return "redirect:/finance/fees";
    }

    @GetMapping("/fees/edit/{id}")
    public String editFeeForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("fee",
                financeService.getFeeById(id).orElseThrow(() -> new IllegalArgumentException("Invalid fee Id:" + id)));
        model.addAttribute("students", financeService.getAllStudents());
        return FEE_FORM_VIEW;
    }

    @RequestMapping(value = "/fees/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteFee(@PathVariable @NonNull Long id) {
        financeService.deleteFee(id);
        return "redirect:/finance/fees";
    }

    @GetMapping("/payments/new")
    public String newPaymentForm(@RequestParam @NonNull Long feeId, Model model) {
        StudentFee fee = financeService.getFeeById(feeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Fee ID: " + feeId));

        com.school.finance.entity.Payment payment = new com.school.finance.entity.Payment();
        payment.setStudentFee(fee);

        model.addAttribute("payment", payment);
        model.addAttribute("fee", fee);
        model.addAttribute("paymentMethods", com.school.finance.enums.PaymentMethod.values());

        return PAYMENT_FORM_VIEW;
    }

    @PostMapping("/payments")
    public String savePayment(
            @jakarta.validation.Valid @ModelAttribute @NonNull com.school.finance.entity.Payment payment,
            org.springframework.validation.BindingResult result, Model model) {

        if (result.hasErrors()) {
            // Verificación de nulabilidad para payment.getStudentFee()
            com.school.finance.entity.StudentFee studentFee = payment.getStudentFee();
            if (studentFee == null) {
                // Manejo del caso donde studentFee es null
                model.addAttribute("errorMessage", "La tarifa del estudiante es requerida");
                model.addAttribute("paymentMethods", com.school.finance.enums.PaymentMethod.values());
                return PAYMENT_FORM_VIEW;
            }

            // Verificación de nulabilidad para studentFee.getId()
            Long feeId = studentFee.getId();
            if (feeId == null) {
                // Manejo del caso donde el ID de la tarifa es null
                model.addAttribute("errorMessage", "La tarifa del estudiante no tiene un ID válido");
                model.addAttribute("paymentMethods", com.school.finance.enums.PaymentMethod.values());
                return PAYMENT_FORM_VIEW;
            }

            StudentFee fee = financeService.getFeeById(feeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Fee ID"));
            model.addAttribute("fee", fee);
            model.addAttribute("paymentMethods", com.school.finance.enums.PaymentMethod.values());
            return PAYMENT_FORM_VIEW;
        }

        financeService.registerPayment(payment);
        return "redirect:/finance/fees";
    }
}
