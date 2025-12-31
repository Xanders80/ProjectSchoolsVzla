package com.school.academic.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.school.academic.entity.Course;
import com.school.academic.entity.GradingScale;
import com.school.academic.entity.StudyPlan;
import com.school.academic.repository.GradingScaleRepository;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.EvaluationWeightRepository;
import com.school.academic.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
public class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private EvaluationWeightRepository evaluationWeightRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private GradingScaleRepository gradingScaleRepository;

    @InjectMocks
    private GradeService gradeService;

    @Test
    @DisplayName("Should return dynamic letter 'A' when score hits custom scale")
    void testGetLetterGradeDynamic() {
        // Arrange
        Course course = new Course();
        StudyPlan plan = new StudyPlan();
        plan.setId(1L);
        course.setStudyPlan(plan);

        GradingScale customScale = new GradingScale();
        customScale.setLabel("E"); // Excelente
        customScale.setMinScore(new BigDecimal("18.00"));
        customScale.setMaxScore(new BigDecimal("20.00"));

        when(gradingScaleRepository.findByStudyPlanAndScore(1L, new BigDecimal("19.0")))
                .thenReturn(customScale);

        // Act
        String letter = gradeService.getLetterGrade(19.0, course);

        // Assert
        assertEquals("E", letter);
    }

    @Test
    @DisplayName("Should return fallback letter 'A' when no dynamic scale found")
    void testGetLetterGradeFallback() {
        // Arrange
        Course course = new Course();
        // No study plan or no matching scale

        // Act
        String letter = gradeService.getLetterGrade(19.0, course);

        // Assert
        assertEquals("A", letter); // Default converter logic
    }
}
