package com.school.academic.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Grade;
import com.school.academic.entity.Student;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.StudentRepository;

@Service
@Transactional(readOnly = true)
public class DocumentService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final GradeService gradeService;
	private final com.school.core.util.DigitalSignatureService digitalSignatureService;

	@Value("${app.school.name:Escuela}")
	private String schoolName;

    public DocumentService(StudentRepository studentRepository,
            GradeRepository gradeRepository,
            GradeService gradeService,
            com.school.core.util.DigitalSignatureService digitalSignatureService) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.gradeService = gradeService;
        this.digitalSignatureService = digitalSignatureService;
    }

    /**
     * Genera los datos para un Certificado de Estudios oficial.
     */
    public Map<String, Object> getCertificateData(@NonNull Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        List<Grade> grades = gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(studentId);

        // Agrupar por curso para obtener nota final
        Map<com.school.academic.entity.Course, Double> courseGrades = grades.stream()
                .filter(g -> g.getCourse() != null)
                .collect(Collectors.groupingBy(
                        Grade::getCourse,
                        Collectors.averagingDouble(Grade::getScore)));

        List<Map<String, Object>> coursesList = new ArrayList<>();
        courseGrades.forEach((course, score) -> {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("name", course.getName());
            courseData.put("score", score);
            courseData.put("letter", gradeService.getLetterGrade(score, course));
            coursesList.add(courseData);
        });

        Map<String, Object> data = new HashMap<>();
        data.put("studentName", student.getFullName());
        data.put("dni", student.getDni());
        data.put("registrationNumber", student.getRegistrationNumber());
        data.put("courses", coursesList);
        data.put("issueDate", LocalDate.now());
		data.put("schoolName", schoolName);

        // Digital Signature for integrity
        String rawData = student.getDni() + "|" + student.getRegistrationNumber() + "|" + LocalDate.now();
        data.put("verificationHash", digitalSignatureService.generateVerificationHash(rawData));

        return data;
    }

    /**
     * Genera los datos para una Acta de Calificaciones de una sección/periodo.
     */
    public Map<String, Object> getSectionActaData(@NonNull Long sectionId, @NonNull Long periodId) {
        // Implementación futura para actas masivas por sección
        return new HashMap<>();
    }
}
