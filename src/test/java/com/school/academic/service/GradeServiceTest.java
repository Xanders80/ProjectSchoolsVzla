package com.school.academic.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.school.academic.dto.GradeBulkEntryDTO;
import com.school.academic.entity.AcademicPeriod;
import com.school.academic.entity.Course;
import com.school.academic.entity.EvaluationWeight;
import com.school.academic.entity.Grade;
import com.school.academic.entity.GradingScale;
import com.school.academic.entity.Student;
import com.school.academic.entity.StudyPlan;
import com.school.academic.enums.EvaluationType;
import com.school.academic.repository.EvaluationWeightRepository;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.StudentRepository;
import com.school.academic.repository.GradingScaleRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

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
	@DisplayName("Should return dynamic letter 'E' when score hits custom scale")
	void testGetLetterGradeDynamic() {
		Course course = new Course();
		StudyPlan plan = new StudyPlan();
		plan.setId(1L);
		course.setStudyPlan(plan);

		GradingScale customScale = new GradingScale();
		customScale.setLabel("E");
		customScale.setMinScore(new BigDecimal("18.00"));
		customScale.setMaxScore(new BigDecimal("20.00"));

		when(gradingScaleRepository.findByStudyPlanAndScore(1L, new BigDecimal("19.0")))
				.thenReturn(customScale);

		String letter = gradeService.getLetterGrade(19.0, course);

		assertEquals("E", letter);
	}

	@Test
	@DisplayName("Should return fallback letter 'A' when no dynamic scale found")
	void testGetLetterGradeFallback() {
		Course course = new Course();

		String letter = gradeService.getLetterGrade(19.0, course);

		assertEquals("A", letter);
	}

	@Test
	@DisplayName("Should return '-' when score is null")
	void testGetLetterGradeNullScore() {
		String letter = gradeService.getLetterGrade(null, new Course());

		assertEquals("-", letter);
	}

	@Test
	@DisplayName("Should return fallback when course has no study plan")
	void testGetLetterGradeNoStudyPlan() {
		Course course = new Course();
		course.setStudyPlan(null);

		String letter = gradeService.getLetterGrade(15.0, course);

		assertEquals("B", letter);
	}

	@Test
	@DisplayName("Should calculate weighted average correctly")
	void testCalculateWeightedAverage() {
		Student student = new Student();
		Course course = new Course();
		AcademicPeriod period = new AcademicPeriod();

		Grade g1 = new Grade();
		g1.setScore(18.0);
		g1.setEvaluationType(EvaluationType.EXAM);

		Grade g2 = new Grade();
		g2.setScore(15.0);
		g2.setEvaluationType(EvaluationType.HOMEWORK);

		when(gradeRepository.findByStudentAndCourseAndPeriod(student, course, period))
				.thenReturn(List.of(g1, g2));

		EvaluationWeight w1 = new EvaluationWeight();
		w1.setEvaluationType(EvaluationType.EXAM);
		w1.setWeight(40.0);

		EvaluationWeight w2 = new EvaluationWeight();
		w2.setEvaluationType(EvaluationType.HOMEWORK);
		w2.setWeight(60.0);

		when(evaluationWeightRepository.findByCourse(course))
				.thenReturn(List.of(w1, w2));

		Double avg = gradeService.calculateWeightedAverage(student, course, period);

		double expected = (18.0 * 0.4 + 15.0 * 0.6);
		assertEquals(expected, avg, 0.01);
	}

	@Test
	@DisplayName("Should return 0 when no grades for weighted average")
	void testCalculateWeightedAverageNoGrades() {
		Student student = new Student();
		Course course = new Course();
		AcademicPeriod period = new AcademicPeriod();

		when(gradeRepository.findByStudentAndCourseAndPeriod(student, course, period))
				.thenReturn(Collections.emptyList());

		Double avg = gradeService.calculateWeightedAverage(student, course, period);

		assertEquals(0.0, avg);
	}

	@Test
	@DisplayName("Should normalize weighted average when weights don't sum to 100")
	void testCalculateWeightedAveragePartialWeights() {
		Student student = new Student();
		Course course = new Course();
		AcademicPeriod period = new AcademicPeriod();

		Grade g1 = new Grade();
		g1.setScore(20.0);
		g1.setEvaluationType(EvaluationType.EXAM);

		when(gradeRepository.findByStudentAndCourseAndPeriod(student, course, period))
				.thenReturn(List.of(g1));

		EvaluationWeight w1 = new EvaluationWeight();
		w1.setEvaluationType(EvaluationType.EXAM);
		w1.setWeight(50.0);

		when(evaluationWeightRepository.findByCourse(course))
				.thenReturn(List.of(w1));

		Double avg = gradeService.calculateWeightedAverage(student, course, period);

		assertEquals(20.0, avg, 0.01);
	}

	@Test
	@DisplayName("Should return 0 when no weights defined")
	void testCalculateWeightedAverageNoWeights() {
		Student student = new Student();
		Course course = new Course();
		AcademicPeriod period = new AcademicPeriod();

		Grade g1 = new Grade();
		g1.setScore(18.0);
		g1.setEvaluationType(EvaluationType.EXAM);

		when(gradeRepository.findByStudentAndCourseAndPeriod(student, course, period))
				.thenReturn(List.of(g1));
		when(evaluationWeightRepository.findByCourse(course))
				.thenReturn(Collections.emptyList());

		Double avg = gradeService.calculateWeightedAverage(student, course, period);

		assertEquals(0.0, avg);
	}

	@Test
	@DisplayName("Should save bulk grades for multiple students")
	void testSaveBulkGrades() {
		AcademicPeriod period = new AcademicPeriod();
		Course course = new Course();

		Student student1 = new Student();
		student1.setId(1L);
		Student student2 = new Student();
		student2.setId(2L);

		when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
		when(studentRepository.findById(2L)).thenReturn(Optional.of(student2));
		when(gradeRepository.findByStudentAndCourseAndPeriod(student1, course, period))
				.thenReturn(Collections.emptyList());
		when(gradeRepository.findByStudentAndCourseAndPeriod(student2, course, period))
				.thenReturn(Collections.emptyList());
		when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GradeBulkEntryDTO bulkDto = new GradeBulkEntryDTO();
		bulkDto.setEvaluationType(EvaluationType.EXAM);

		GradeBulkEntryDTO.StudentGradeDTO sg1 = new GradeBulkEntryDTO.StudentGradeDTO();
		sg1.setStudentId(1L);
		sg1.setScore(18.0);
		sg1.setComments("Good");

		GradeBulkEntryDTO.StudentGradeDTO sg2 = new GradeBulkEntryDTO.StudentGradeDTO();
		sg2.setStudentId(2L);
		sg2.setScore(15.0);
		sg2.setComments("Average");

		bulkDto.setStudentGrades(List.of(sg1, sg2));

		gradeService.saveBulkGrades(bulkDto, course, period);

		verify(gradeRepository, times(2)).save(any(Grade.class));
	}

	@Test
	@DisplayName("Should skip null scores in bulk grades")
	void testSaveBulkGradesSkipNullScore() {
		AcademicPeriod period = new AcademicPeriod();
		Course course = new Course();

		GradeBulkEntryDTO bulkDto = new GradeBulkEntryDTO();
		bulkDto.setEvaluationType(EvaluationType.EXAM);

		GradeBulkEntryDTO.StudentGradeDTO sg1 = new GradeBulkEntryDTO.StudentGradeDTO();
		sg1.setStudentId(1L);
		sg1.setScore(null);

		bulkDto.setStudentGrades(List.of(sg1));

		gradeService.saveBulkGrades(bulkDto, course, period);

		verify(gradeRepository, never()).save(any(Grade.class));
	}

	@Test
	@DisplayName("Should skip null student IDs in bulk grades")
	void testSaveBulkGradesSkipNullStudentId() {
		AcademicPeriod period = new AcademicPeriod();
		Course course = new Course();

		GradeBulkEntryDTO bulkDto = new GradeBulkEntryDTO();
		bulkDto.setEvaluationType(EvaluationType.EXAM);

		GradeBulkEntryDTO.StudentGradeDTO sg1 = new GradeBulkEntryDTO.StudentGradeDTO();
		sg1.setStudentId(null);
		sg1.setScore(15.0);

		bulkDto.setStudentGrades(List.of(sg1));

		gradeService.saveBulkGrades(bulkDto, course, period);

		verify(gradeRepository, never()).save(any(Grade.class));
	}

	@Test
	@DisplayName("Should throw when student not found in bulk grades")
	void testSaveBulkGradesStudentNotFound() {
		AcademicPeriod period = new AcademicPeriod();
		Course course = new Course();

		when(studentRepository.findById(999L)).thenReturn(Optional.empty());

		GradeBulkEntryDTO bulkDto = new GradeBulkEntryDTO();
		bulkDto.setEvaluationType(EvaluationType.EXAM);

		GradeBulkEntryDTO.StudentGradeDTO sg1 = new GradeBulkEntryDTO.StudentGradeDTO();
		sg1.setStudentId(999L);
		sg1.setScore(15.0);

		bulkDto.setStudentGrades(List.of(sg1));

		assertThrows(IllegalArgumentException.class,
				() -> gradeService.saveBulkGrades(bulkDto, course, period));
	}

	@Test
	@DisplayName("Should update existing grade when same evaluation type in bulk")
	void testSaveBulkGradesUpdateExisting() {
		AcademicPeriod period = new AcademicPeriod();
		Course course = new Course();

		Student student = new Student();
		student.setId(1L);

		Grade existingGrade = new Grade();
		existingGrade.setEvaluationType(EvaluationType.EXAM);
		existingGrade.setScore(10.0);

		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
		when(gradeRepository.findByStudentAndCourseAndPeriod(student, course, period))
				.thenReturn(List.of(existingGrade));
		when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GradeBulkEntryDTO bulkDto = new GradeBulkEntryDTO();
		bulkDto.setEvaluationType(EvaluationType.EXAM);

		GradeBulkEntryDTO.StudentGradeDTO sg1 = new GradeBulkEntryDTO.StudentGradeDTO();
		sg1.setStudentId(1L);
		sg1.setScore(19.0);

		bulkDto.setStudentGrades(List.of(sg1));

		gradeService.saveBulkGrades(bulkDto, course, period);

		assertEquals(19.0, existingGrade.getScore());
		verify(gradeRepository).save(existingGrade);
	}
}
