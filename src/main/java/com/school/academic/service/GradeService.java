package com.school.academic.service;

import com.school.academic.entity.*;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.EvaluationWeightRepository;
import com.school.academic.util.GradingScaleConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final EvaluationWeightRepository evaluationWeightRepository;
    private final com.school.academic.repository.StudentRepository studentRepository;

    public GradeService(GradeRepository gradeRepository,
            EvaluationWeightRepository evaluationWeightRepository,
            com.school.academic.repository.StudentRepository studentRepository) {
        this.gradeRepository = gradeRepository;
        this.evaluationWeightRepository = evaluationWeightRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public Double calculateWeightedAverage(Student student, Course course, AcademicPeriod period) {
        List<Grade> grades = gradeRepository.findByStudentAndCourseAndPeriod(student, course, period);
        List<EvaluationWeight> weights = evaluationWeightRepository.findByCourse(course);

        if (grades.isEmpty())
            return 0.0;

        Map<String, Double> weightMap = weights.stream()
                .collect(Collectors.toMap(w -> w.getEvaluationType().name(), EvaluationWeight::getWeight));

        double totalWeightedScore = 0.0;
        double totalWeights = 0.0;

        for (Grade grade : grades) {
            Double weight = weightMap.getOrDefault(grade.getEvaluationType().name(), 0.0);
            totalWeightedScore += grade.getScore() * (weight / 100.0);
            totalWeights += weight;
        }

        // Normalize if weights don't sum to 100% yet
        return totalWeights > 0 ? (totalWeightedScore / (totalWeights / 100.0)) : 0.0;
    }

    @Transactional
    public void saveBulkGrades(com.school.academic.dto.GradeBulkEntryDTO bulkDto, Course course,
            AcademicPeriod period) {
        for (com.school.academic.dto.GradeBulkEntryDTO.StudentGradeDTO sgDto : bulkDto.getStudentGrades()) {
            if (sgDto.getScore() == null)
                continue;

            Long studentId = sgDto.getStudentId();
            if (studentId == null)
                continue;
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("Estudiante no encontrado: " + sgDto.getStudentId()));

            // Find existing or create new
            Grade grade = gradeRepository.findByStudentAndCourseAndPeriod(student, course, period)
                    .stream()
                    .filter(g -> g.getEvaluationType() == bulkDto.getEvaluationType())
                    .findFirst()
                    .orElse(new Grade());

            grade.setStudent(student);
            grade.setCourse(course);
            grade.setPeriod(period);
            grade.setEvaluationType(bulkDto.getEvaluationType());
            grade.setScore(sgDto.getScore());
            grade.setComments(sgDto.getComments());

            gradeRepository.save(grade);
        }
    }

    public String getLetterGrade(Double score) {
        return GradingScaleConverter.toLetter(score);
    }
}
