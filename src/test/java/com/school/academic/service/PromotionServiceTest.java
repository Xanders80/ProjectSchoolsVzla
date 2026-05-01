package com.school.academic.service;

import com.school.academic.entity.Promotion;
import com.school.academic.entity.Student;
import com.school.academic.entity.Section;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.PromotionRepository;
import com.school.academic.repository.StudentRepository;
import com.school.core.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
	}

	@Test
	void testFindAll() {
		Student s1 = new Student();
		Student s2 = new Student();
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
		service.delete(1L);
		assertTrue(p.isDeleted());
		assertNotNull(p.getDeletedAt());
		verify(promotionRepository).save(p);
	}
}
