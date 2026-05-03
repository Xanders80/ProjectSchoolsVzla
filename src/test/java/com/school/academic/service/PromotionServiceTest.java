package com.school.academic.service;

import com.school.academic.entity.Grade;
import com.school.academic.entity.Promotion;
import com.school.academic.entity.Student;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.PromotionRepository;
import com.school.academic.repository.StudentRepository;
import com.school.core.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromotionServiceTest {

	@Mock
	private PromotionRepository promotionRepository;
	@Mock
	private GradeRepository gradeRepository;
	@Mock
	private StudentRepository studentRepository;
	@Mock
	private AuditService auditService;

	@InjectMocks
	private PromotionService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(service, "averageThreshold", 70.0);
		ReflectionTestUtils.setField(service, "maxFailedCourses", 2);
		ReflectionTestUtils.setField(service, "maxFailedCoursesRetain", 4);
	}

	@Test
	void testFindAll() {
		Promotion p1 = new Promotion();
		Promotion p2 = new Promotion();
		when(promotionRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

		List<Promotion> result = service.findAll();
		assertEquals(2, result.size());
	}

	@Test
	void testSave() {
		Promotion p = new Promotion();
		when(promotionRepository.save(p)).thenReturn(p);

		Promotion saved = service.save(p);
		assertNotNull(saved);
	}

	@Test
	void testDelete() {
		Promotion p = new Promotion();
		when(promotionRepository.findById(1L)).thenReturn(Optional.of(p));
		when(promotionRepository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

		service.delete(1L);

		ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
		verify(promotionRepository).save(captor.capture());
		assertTrue(captor.getValue().isDeleted());
		assertNotNull(captor.getValue().getDeletedAt());
	}

	@Test
	void testDeleteNonexistent() {
		when(promotionRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> service.delete(999L));
	}

	@Test
	void testEvaluatePromotionPromoted() {
		Grade g1 = new Grade();
		g1.setScore(80.0);
		g1.setPeriod(new com.school.academic.entity.AcademicPeriod());
		g1.getPeriod().setId(1L);

		Grade g2 = new Grade();
		g2.setScore(75.0);
		g2.setPeriod(g1.getPeriod());

		when(gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(1L))
				.thenReturn(Arrays.asList(g1, g2));

		PromotionService.PromotionResult result = service.evaluatePromotion(1L, 1L);

		assertEquals("PROMOTED", result.getStatus());
		assertEquals(77.5, result.getAverage(), 0.01);
		assertEquals(0, result.getFailedCourses());
	}

	@Test
	void testEvaluatePromotionPendingRecovery() {
		Grade g1 = new Grade();
		g1.setScore(65.0);
		g1.setPeriod(new com.school.academic.entity.AcademicPeriod());
		g1.getPeriod().setId(1L);

		Grade g2 = new Grade();
		g2.setScore(50.0);
		g2.setPeriod(g1.getPeriod());

		when(gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(1L))
				.thenReturn(Arrays.asList(g1, g2));

		PromotionService.PromotionResult result = service.evaluatePromotion(1L, 1L);

		assertEquals("PENDING_RECOVERY", result.getStatus());
		assertEquals(1, result.getFailedCourses());
	}

	@Test
	void testEvaluatePromotionRetained() {
		Grade g1 = new Grade();
		g1.setScore(40.0);
		g1.setPeriod(new com.school.academic.entity.AcademicPeriod());
		g1.getPeriod().setId(1L);

		Grade g2 = new Grade();
		g2.setScore(35.0);
		g2.setPeriod(g1.getPeriod());

		Grade g3 = new Grade();
		g3.setScore(30.0);
		g3.setPeriod(g1.getPeriod());

		Grade g4 = new Grade();
		g4.setScore(25.0);
		g4.setPeriod(g1.getPeriod());

		Grade g5 = new Grade();
		g5.setScore(55.0);
		g5.setPeriod(g1.getPeriod());

		when(gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(1L))
				.thenReturn(Arrays.asList(g1, g2, g3, g4, g5));

		PromotionService.PromotionResult result = service.evaluatePromotion(1L, 1L);

		assertEquals("RETAINED", result.getStatus());
		assertEquals(5, result.getFailedCourses());
	}

	@Test
	void testEvaluatePromotionNoData() {
		when(gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(1L))
				.thenReturn(List.of());

		PromotionService.PromotionResult result = service.evaluatePromotion(1L, 1L);

		assertEquals("NO_DATA", result.getStatus());
		assertEquals(0.0, result.getAverage());
		assertEquals(0, result.getFailedCourses());
	}

	@Test
	void testEvaluatePromotionFiltersByPeriod() {
		com.school.academic.entity.AcademicPeriod period1 = new com.school.academic.entity.AcademicPeriod();
		period1.setId(1L);

		com.school.academic.entity.AcademicPeriod period2 = new com.school.academic.entity.AcademicPeriod();
		period2.setId(2L);

		Grade g1 = new Grade();
		g1.setScore(80.0);
		g1.setPeriod(period1);

		Grade g2 = new Grade();
		g2.setScore(50.0);
		g2.setPeriod(period2);

		when(gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(1L))
				.thenReturn(Arrays.asList(g1, g2));

		PromotionService.PromotionResult result = service.evaluatePromotion(1L, 1L);

		assertEquals("PROMOTED", result.getStatus());
		assertEquals(80.0, result.getAverage(), 0.01);
		assertEquals(0, result.getFailedCourses());
	}

	@Test
	void testProcessMassPromotion() {
		Student s1 = new Student();
		s1.setId(1L);
		Student s2 = new Student();
		s2.setId(2L);

		when(studentRepository.findAllActive()).thenReturn(Arrays.asList(s1, s2));
		when(gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(1L))
				.thenReturn(List.of());
		when(gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(2L))
				.thenReturn(List.of());

		service.processMassPromotion(1L);

		verify(auditService, times(2)).logGenericAction(eq("ACADEMIC_PROMOTION"), anyString(), anyString());
	}
}
