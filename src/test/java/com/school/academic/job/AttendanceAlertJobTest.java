package com.school.academic.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.school.academic.entity.Attendance;
import com.school.academic.entity.Student;
import com.school.academic.enums.AttendanceStatus;
import com.school.academic.repository.AttendanceRepository;
import com.school.communication.entity.Notification;
import com.school.communication.repository.NotificationRepository;
import com.school.core.entity.User;

@ExtendWith(MockitoExtension.class)
public class AttendanceAlertJobTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private AttendanceAlertJob attendanceAlertJob;

    @Test
    @DisplayName("Should create alert when absences exceed threshold")
    void testCheckAttendanceAndNotify() {
        // Arrange
        Student student = new Student();
        student.setId(1L);
        student.setRegistrationNumber("ST-001");

        User user = new User();
        user.setId(10L);
        student.setUser(user);

        Attendance todayAbsence = new Attendance();
        todayAbsence.setStudent(student);
        todayAbsence.setStatus(AttendanceStatus.ABSENT);

        when(attendanceRepository.findByDateAndStatus(any(LocalDate.class), eq(AttendanceStatus.ABSENT)))
                .thenReturn(Collections.singletonList(todayAbsence));

        // Threshold is 3. Let's return 3.
        when(attendanceRepository.countByStudentIdAndStatusAndDateAfter(eq(1L), eq(AttendanceStatus.ABSENT),
                any(LocalDate.class)))
                .thenReturn(3L);

        // Act
        attendanceAlertJob.checkAttendanceAndNotify();

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should NOT create alert when absences are below threshold")
    void testCheckAttendanceAndNotifyBelowThreshold() {
        // Arrange
        Student student = new Student();
        student.setId(2L);

        Attendance todayAbsence = new Attendance();
        todayAbsence.setStudent(student);

        when(attendanceRepository.findByDateAndStatus(any(LocalDate.class), eq(AttendanceStatus.ABSENT)))
                .thenReturn(Collections.singletonList(todayAbsence));

        when(attendanceRepository.countByStudentIdAndStatusAndDateAfter(any(), any(), any()))
                .thenReturn(2L); // 2 < 3

        // Act
        attendanceAlertJob.checkAttendanceAndNotify();

        // Assert
        verify(notificationRepository, times(0)).save(any(Notification.class));
    }
}
