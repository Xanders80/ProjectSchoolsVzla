package com.school.finance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Student;
import com.school.academic.repository.StudentRepository;
import com.school.finance.entity.StudentFee;
import com.school.finance.repository.StudentFeeRepository;

@Service
@Transactional
public class FinanceService {

    private final StudentFeeRepository studentFeeRepository;
    private final StudentRepository studentRepository;
    private final com.school.finance.repository.PaymentRepository paymentRepository;

    public FinanceService(StudentFeeRepository studentFeeRepository,
            StudentRepository studentRepository,
            com.school.finance.repository.PaymentRepository paymentRepository) {
        this.studentFeeRepository = studentFeeRepository;
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
    }

    public StudentFee createFee(@org.springframework.lang.NonNull StudentFee fee) {
        return studentFeeRepository.save(fee);
    }

    public com.school.finance.entity.Payment registerPayment(
            @org.springframework.lang.NonNull com.school.finance.entity.Payment payment) {
        StudentFee fee = studentFeeRepository.findById(payment.getStudentFee().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Fee ID"));

        payment.setStudentFee(fee);
        com.school.finance.entity.Payment savedPayment = paymentRepository.save(payment);

        updateFeeStatus(fee);

        return savedPayment;
    }

    private void updateFeeStatus(StudentFee fee) {
        java.math.BigDecimal totalPaid = paymentRepository.findByStudentFeeId(fee.getId()).stream()
                .map(com.school.finance.entity.Payment::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        if (totalPaid.compareTo(fee.getAmount()) >= 0) {
            fee.setStatus(com.school.finance.enums.FeeStatus.PAID);
            if (fee.getPaymentDate() == null) {
                fee.setPaymentDate(java.time.LocalDate.now());
            }
        } else if (totalPaid.compareTo(java.math.BigDecimal.ZERO) > 0) {
            fee.setStatus(com.school.finance.enums.FeeStatus.PARTIAL);
        } else {
            fee.setStatus(com.school.finance.enums.FeeStatus.PENDING);
        }
        studentFeeRepository.save(fee);
    }

    public List<StudentFee> getFeesByStudent(@org.springframework.lang.NonNull Long studentId) {
        return studentFeeRepository.findByStudentIdAndDeletedFalse(studentId);
    }

    public List<StudentFee> getAllFees() {
        return studentFeeRepository.findByDeletedFalse(org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    public org.springframework.data.domain.Page<StudentFee> getAllFees(
            @org.springframework.lang.NonNull org.springframework.data.domain.Pageable pageable) {
        return studentFeeRepository.findByDeletedFalse(pageable);
    }

    public Optional<StudentFee> getFeeById(@org.springframework.lang.NonNull Long id) {
        return studentFeeRepository.findById(id);
    }

    public void deleteFee(@org.springframework.lang.NonNull Long id) {
        StudentFee fee = studentFeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fee not found"));
        fee.setDeleted(true);
        fee.setDeletedAt(java.time.LocalDateTime.now());
        fee.setDeletedBy(getCurrentUser());
        studentFeeRepository.save(fee);
    }

    // Helper to get all students for dropdowns
    public List<Student> getAllStudents() {
        return studentRepository.findByDeletedFalse(org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    @org.springframework.lang.NonNull
    private String getCurrentUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null) {
            String name = auth.getName();
            if (name != null) {
                return name;
            }
        }
        return "system";
    }
}
