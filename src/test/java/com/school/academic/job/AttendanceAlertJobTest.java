package com.school.academic.job;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.school.academic.entity.Student;
import com.school.academic.enums.AttendanceStatus;
import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.StudentRepository;
import com.school.communication.service.NotificationService;
import com.school.core.entity.User;

@ExtendWith(MockitoExtension.class)
public class AttendanceAlertJobTest {

	@Mock
	private AttendanceRepository attendanceRepository;
	@Mock
	private StudentRepository studentRepository;
	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private AttendanceAlertJob attendanceAlertJob;

	@Test
	@DisplayName("Should create alert when absences exceed threshold")
	void testCheckAttendanceAndNotify() {
		Student student = new Student();
		student.setId(1L);
		student.setFirstName("Juan");
		student.setLastName("Perez");

		User user = new User();
		user.setId(10L);
		student.setUser(user);

		when(studentRepository.findStudentsWithAbsencesMoreThan(any(LocalDate.class), any(LocalDate.class), eq(3L)))
				.thenReturn(List.of(student));
		when(attendanceRepository.countAbsencesByStudentAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(3L);

		attendanceAlertJob.checkAttendanceAlerts();

		verify(notificationService).createNotification(
				eq("Alerta de Asistencia Crítica"), anyString(),
				eq(com.school.communication.enums.NotificationType.ATTENDANCE_ALERT), eq(user));
	}

	@Test
	@DisplayName("Should NOT create alert when no students exceed threshold")
	void testCheckAttendanceAndNotifyBelowThreshold() {
		when(studentRepository.findStudentsWithAbsencesMoreThan(any(LocalDate.class), any(LocalDate.class), eq(3L)))
				.thenReturn(Collections.emptyList());

		attendanceAlertJob.checkAttendanceAlerts();

		verify(notificationService, never()).createNotification(anyString(), anyString(), any(), any());
	}

	@Test
	@DisplayName("Should skip notification when student has no user")
	void testSkipNotificationWhenNoUser() {
		Student student = new Student();
		student.setId(1L);
		student.setFirstName("Ana");
		student.setLastName("Garcia");
		student.setUser(null);

		when(studentRepository.findStudentsWithAbsencesMoreThan(any(LocalDate.class), any(LocalDate.class), eq(3L)))
				.thenReturn(List.of(student));
		when(attendanceRepository.countAbsencesByStudentAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(4L);

		attendanceAlertJob.checkAttendanceAlerts();

		verify(notificationService, never()).createNotification(anyString(), anyString(), any(), any());
	}
}
