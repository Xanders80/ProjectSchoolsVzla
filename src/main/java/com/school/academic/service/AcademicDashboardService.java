package com.school.academic.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.StudentRepository;

@Service
@Transactional(readOnly = true)
public class AcademicDashboardService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;

    public AcademicDashboardService(StudentRepository studentRepository,
                                  GradeRepository gradeRepository,
                                  AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public Map<String, Object> getAcademicMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Estudiantes activos
        long totalStudents = studentRepository.countByDeletedFalse();
        metrics.put("totalStudents", totalStudents);
        
        // Promedio general de calificaciones
        Double averageGrade = gradeRepository.findAverageGrade();
        metrics.put("averageGrade", averageGrade != null ? Math.round(averageGrade * 100.0) / 100.0 : 0.0);
        
        // Tasa de asistencia del mes actual
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        Double attendanceRate = calculateAttendanceRate(monthStart, LocalDate.now());
        metrics.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);
        
        // Estudiantes con alertas de asistencia
        long studentsWithAlerts = countStudentsWithAttendanceAlerts();
        metrics.put("studentsWithAlerts", studentsWithAlerts);
        
        return metrics;
    }

    private Double calculateAttendanceRate(LocalDate startDate, LocalDate endDate) {
        long totalAttendance = attendanceRepository.countByDateBetween(startDate, endDate);
        long presentAttendance = attendanceRepository.countByDateBetweenAndStatus(
            startDate, endDate, com.school.academic.enums.AttendanceStatus.PRESENT);
        
        return totalAttendance > 0 ? (double) presentAttendance / totalAttendance * 100 : 0.0;
    }

    private long countStudentsWithAttendanceAlerts() {
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        return studentRepository.countStudentsWithExcessiveAbsences(weekAgo, LocalDate.now());
    }
}