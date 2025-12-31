package com.school.academic.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.AcademicPeriod;
import com.school.academic.entity.Grade;
import com.school.academic.entity.Student;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.StudentRepository;

@Service
@Transactional(readOnly = true)
public class CertificateService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    // private final AcademicService academicService;

    public CertificateService(StudentRepository studentRepository, GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
    }

    public Map<String, Object> generateCourseCertificateData(@NonNull Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // This is a simplified logic. In a real world we would filter by Period too.
        // For now, let's fetch course grades.
        List<Grade> grades = gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(studentId);
        // Filter by course... logic depends on query repository updates or filtering
        // here

        Map<String, Object> data = new HashMap<>();
        data.put("studentName", student.getFullName());
        data.put("dni", student.getDni());
        data.put("date", LocalDate.now());
        data.put("courseId", courseId);
        // data.put("finalScore", ...);

        return data;
    }

    public Map<String, Object> generateAnnualReport(Long studentId, Long periodId) {
        Map<String, Object> report = new HashMap<>();
        // Logic to aggregate all grades for an academic period
        return report;
    }
}
