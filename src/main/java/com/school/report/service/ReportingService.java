package com.school.report.service;

import com.school.finance.enums.FeeStatus;
import com.school.finance.repository.StudentFeeRepository;
import com.school.report.dto.ChartDataDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportingService {

    private final com.school.academic.repository.EnrollmentRepository enrollmentRepository;
    private final StudentFeeRepository feeRepository;

    public ReportingService(com.school.academic.repository.EnrollmentRepository enrollmentRepository,
            StudentFeeRepository feeRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.feeRepository = feeRepository;
    }

    public ChartDataDTO getStudentEnrollmentData() {
        List<Object[]> results = enrollmentRepository.countStudentsBySection();

        List<String> labels = results.stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());

        List<Number> data = results.stream()
                .map(row -> (Number) row[1])
                .collect(Collectors.toList());

        return new ChartDataDTO(labels, data);
    }

    public ChartDataDTO getFeeStatusData() {
        List<Object[]> results = feeRepository.countFeesByStatus();
        List<String> labels = new ArrayList<>();
        List<Number> data = new ArrayList<>();

        for (Object[] row : results) {
            labels.add(row[0].toString());
            data.add((Number) row[1]);
        }
        return new ChartDataDTO(labels, data);
    }

    public Map<String, BigDecimal> getFinancialSummary() {
        Map<String, BigDecimal> summary = new HashMap<>();

        BigDecimal collected = feeRepository.sumAmountByStatus(FeeStatus.PAID);
        BigDecimal pending = feeRepository.sumAmountByStatus(FeeStatus.PENDING);
        // Actually sumAmount returns total amount of the fee itself, w/o tracking
        // payments individually in this simplified query.
        // For MVP:
        // PAID = Real money in
        // PENDING = Expected money

        summary.put("collected", collected != null ? collected : BigDecimal.ZERO);
        summary.put("pending", pending != null ? pending : BigDecimal.ZERO);

        return summary;
    }
}
