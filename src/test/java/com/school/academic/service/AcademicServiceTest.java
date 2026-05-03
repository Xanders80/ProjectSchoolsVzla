package com.school.academic.service;

import com.school.academic.entity.*;
import com.school.academic.enums.EnrollmentStatus;
import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.EnrollmentRepository;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.StudentRepository;
import com.school.core.service.AuditService;
import com.school.health.service.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcademicServiceTest {

	@Mock
	private StudentRepository studentRepository;
	@Mock
	private StudentService studentService;
	@Mock
	private CourseService courseService;
	@Mock
	private SectionService sectionService;
	@Mock
	private GradeRepository gradeRepository;
	@Mock
	private AttendanceRepository attendanceRepository;
	@Mock
	private EnrollmentRepository enrollmentRepository;
	@Mock
	private HealthService healthService;
	@Mock
	private AuditService auditService;

	@InjectMocks
	private AcademicService academicService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldSaveGrade() {
		Grade grade = new Grade();
		when(gradeRepository.save(grade)).thenReturn(grade);

		Grade saved = academicService.saveGrade(grade);
		assertNotNull(saved);
		verify(gradeRepository).save(grade);
	}

	@Test
	void shouldSoftDeleteGrade() {
		Grade grade = new Grade();
		grade.setId(1L);
		when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));
		when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> invocation.getArgument(0));

		academicService.deleteGrade(1L);

		ArgumentCaptor<Grade> captor = ArgumentCaptor.forClass(Grade.class);
		verify(gradeRepository).save(captor.capture());
		assertTrue(captor.getValue().isDeleted());
		assertNotNull(captor.getValue().getDeletedAt());
	}

	@Test
	void shouldThrowWhenDeleteNonexistentGrade() {
		when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> academicService.deleteGrade(999L));
	}

	@Test
	void shouldEnrollStudent() {
		Student student = new Student();
		student.setId(1L);

		AcademicPeriod period = new AcademicPeriod();
		period.setId(10L);

		Section section = new Section();
		section.setId(1L);
		section.setPeriod(period);

		when(sectionService.getSectionById(1L)).thenReturn(Optional.of(section));
		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
		when(enrollmentRepository.findActiveStudentIdsByPeriodId(10L)).thenReturn(List.of());

		academicService.enrollStudent(1L, 1L);

		verify(enrollmentRepository).save(any(Enrollment.class));
		verify(auditService).logGenericAction(eq("ENROLL_STUDENT"), anyString(), anyString());
	}

	@Test
	void shouldPreventDuplicateEnrollment() {
		Student student = new Student();
		student.setId(1L);

		AcademicPeriod period = new AcademicPeriod();
		period.setId(10L);

		Section section = new Section();
		section.setId(1L);
		section.setPeriod(period);

		when(sectionService.getSectionById(1L)).thenReturn(Optional.of(section));
		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
		when(enrollmentRepository.findActiveStudentIdsByPeriodId(10L)).thenReturn(List.of(1L));

		assertThrows(IllegalStateException.class, () -> academicService.enrollStudent(1L, 1L));
		verify(enrollmentRepository, never()).save(any());
	}

	@Test
	void shouldThrowWhenEnrollInNonexistentSection() {
		when(sectionService.getSectionById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> academicService.enrollStudent(1L, 999L));
	}

	@Test
	void shouldThrowWhenEnrollNonexistentStudent() {
		Section section = new Section();
		section.setId(1L);
		AcademicPeriod period = new AcademicPeriod();
		period.setId(10L);
		section.setPeriod(period);

		when(sectionService.getSectionById(1L)).thenReturn(Optional.of(section));
		when(studentRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> academicService.enrollStudent(999L, 1L));
	}

	@Test
	void shouldTransferStudent() {
		Student student = new Student();
		student.setId(1L);

		Enrollment enrollment = new Enrollment();
		enrollment.setStudent(student);
		enrollment.setSection(new Section());

		Section toSection = new Section();
		toSection.setId(2L);

		when(enrollmentRepository.findBySectionId(1L)).thenReturn(List.of(enrollment));
		when(sectionService.getSectionById(2L)).thenReturn(Optional.of(toSection));
		when(enrollmentRepository.findBySectionId(2L)).thenReturn(List.of());

		academicService.transferStudent(1L, 1L, 2L);

		assertEquals(toSection, enrollment.getSection());
		assertNotNull(enrollment.getEnrollmentDate());
		verify(enrollmentRepository).save(enrollment);
		verify(auditService).logGenericAction(eq("TRANSFER_STUDENT"), anyString(), anyString());
	}

	@Test
	void shouldPreventTransferToSameSection() {
		assertThrows(IllegalArgumentException.class, () -> academicService.transferStudent(1L, 1L, 1L));
	}

	@Test
	void shouldPreventTransferWhenNotInSourceSection() {
		when(enrollmentRepository.findBySectionId(1L)).thenReturn(List.of());

		assertThrows(IllegalArgumentException.class, () -> academicService.transferStudent(1L, 1L, 2L));
	}

	@Test
	void shouldPreventTransferWhenAlreadyInTargetSection() {
		Student student = new Student();
		student.setId(1L);

		Enrollment fromEnrollment = new Enrollment();
		fromEnrollment.setStudent(student);

		Enrollment toEnrollment = new Enrollment();
		Student toStudent = new Student();
		toStudent.setId(1L);
		toEnrollment.setStudent(toStudent);

		Section toSection = new Section();
		toSection.setId(2L);

		when(enrollmentRepository.findBySectionId(1L)).thenReturn(List.of(fromEnrollment));
		when(sectionService.getSectionById(2L)).thenReturn(Optional.of(toSection));
		when(enrollmentRepository.findBySectionId(2L)).thenReturn(List.of(toEnrollment));

		assertThrows(IllegalStateException.class, () -> academicService.transferStudent(1L, 1L, 2L));
	}

	@Test
	void shouldSoftDeleteStudent() {
		Student student = new Student();
		student.setId(1L);

		when(enrollmentRepository.existsByStudentId(1L)).thenReturn(false);
		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

		academicService.deleteStudent(1L);

		assertTrue(student.isDeleted());
		verify(studentRepository).save(student);
		verify(auditService).logStudentDeletion(eq(1L), anyString());
	}

	@Test
	void shouldPreventDeleteStudentWithEnrollments() {
		when(enrollmentRepository.existsByStudentId(1L)).thenReturn(true);

		assertThrows(IllegalStateException.class, () -> academicService.deleteStudent(1L));
	}

	@Test
	void shouldThrowWhenDeleteNonexistentStudent() {
		when(enrollmentRepository.existsByStudentId(1L)).thenReturn(false);
		when(studentRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> academicService.deleteStudent(1L));
	}

	@Test
	void shouldHardDeleteStudent() {
		doNothing().when(healthService).deleteStudentHealthData(1L);
		doNothing().when(gradeRepository).deleteByStudentId(1L);
		doNothing().when(attendanceRepository).deleteByStudentId(1L);
		doNothing().when(enrollmentRepository).deleteByStudentId(1L);
		doNothing().when(studentRepository).deleteById(1L);

		academicService.hardDeleteStudent(1L);

		verify(healthService).deleteStudentHealthData(1L);
		verify(gradeRepository).deleteByStudentId(1L);
		verify(attendanceRepository).deleteByStudentId(1L);
		verify(enrollmentRepository).deleteByStudentId(1L);
		verify(studentRepository).deleteById(1L);
		verify(auditService).logStudentDeletion(eq(1L), anyString());
	}

	@Test
	void shouldUnenrollStudent() {
		Student student = new Student();
		student.setId(1L);

		Section section = new Section();
		section.setId(2L);

		Enrollment enrollment = new Enrollment();
		enrollment.setStudent(student);
		enrollment.setSection(section);

		when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

		academicService.unenrollStudent(1L);

		verify(enrollmentRepository).delete(enrollment);
		verify(auditService).logGenericAction(eq("UNENROLL_STUDENT"), anyString(), anyString());
	}

	@Test
	void shouldThrowWhenUnenrollWithNullStudent() {
		Enrollment enrollment = new Enrollment();
		enrollment.setStudent(null);

		when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

		assertThrows(IllegalStateException.class, () -> academicService.unenrollStudent(1L));
	}

	@Test
	void shouldBatchReenroll() {
		AcademicPeriod period = new AcademicPeriod();
		period.setId(10L);

		Section section = new Section();
		section.setId(1L);
		section.setPeriod(period);

		Student student1 = new Student();
		student1.setId(1L);
		Student student2 = new Student();
		student2.setId(2L);

		when(sectionService.getSectionById(1L)).thenReturn(Optional.of(section));
		when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
		when(studentRepository.findById(2L)).thenReturn(Optional.of(student2));
		when(enrollmentRepository.findBySectionId(1L)).thenReturn(List.of());

		academicService.batchReenroll(1L, List.of(1L, 2L));

		verify(enrollmentRepository, times(2)).save(any(Enrollment.class));
		verify(auditService).logGenericAction(eq("BATCH_REENROLL"), anyString(), anyString());
	}

	@Test
	void shouldSkipAlreadyEnrolledInBatchReenroll() {
		AcademicPeriod period = new AcademicPeriod();
		period.setId(10L);

		Section section = new Section();
		section.setId(1L);
		section.setPeriod(period);

		Student student1 = new Student();
		student1.setId(1L);

		Enrollment existing = new Enrollment();
		Student existingStudent = new Student();
		existingStudent.setId(1L);
		existing.setStudent(existingStudent);

		when(sectionService.getSectionById(1L)).thenReturn(Optional.of(section));
		when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
		when(enrollmentRepository.findBySectionId(1L)).thenReturn(List.of(existing));

		academicService.batchReenroll(1L, List.of(1L));

		verify(enrollmentRepository, never()).save(any(Enrollment.class));
	}
}
