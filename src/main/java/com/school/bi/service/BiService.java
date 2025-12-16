package com.school.bi.service;

import com.school.academic.repository.GradeRepository;
import com.school.admin.repository.StaffRepository;
import com.school.academic.repository.StudentRepository;
import com.school.finance.repository.StudentFeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BiService {

    private final StudentFeeRepository feeRepository;
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;

    public BiService(StudentFeeRepository feeRepository,
            GradeRepository gradeRepository,
            StudentRepository studentRepository,
            StaffRepository staffRepository) {
        this.feeRepository = feeRepository;
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
    }

    public Map<String, Object> getFinancialTrend() {
        List<Object[]> data = feeRepository.sumPaidFeesByMonth();
        // Limit to last 12 months in logic if needed, but SQL ordered desc by date
        // Note: The SQL returns YEAR, MONTH, SUM.

        // Transform to Labels (MM-YYYY) and Data (Sum)
        List<String> labels = data.stream()
                .map(row -> row[1] + "-" + row[0])
                .collect(Collectors.toList());

        List<BigDecimal> values = data.stream()
                .map(row -> (BigDecimal) row[2])
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", values);
        return result;
    }

    public Map<String, Object> getAcademicTrend() {
        List<Object[]> data = gradeRepository.findAverageGradeByCourse();
        // Row: CourseTitle, AvgScore

        List<String> labels = data.stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());

        List<Double> values = data.stream()
                .map(row -> ((Number) row[1]).doubleValue())
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", values);
        return result;
    }

    public Map<String, Long> getKpiStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalStaff", staffRepository.count());
        // For GPA or Income, we could add here too but they return BigDec/Double
        return stats;
    }
}
