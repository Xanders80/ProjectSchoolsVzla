package com.school.academic.job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.AcademicPeriod;
import com.school.academic.entity.Attendance;
import com.school.academic.enums.AttendanceStatus;
import com.school.academic.repository.AttendanceRepository;
import com.school.communication.entity.Message;
import com.school.communication.entity.Notification;
import com.school.communication.enums.NotificationType;
import com.school.communication.enums.Priority;
import com.school.communication.repository.NotificationRepository;

@Component
public class AttendanceAlertJob {

    private final AttendanceRepository attendanceRepository;
    private final NotificationRepository notificationRepository;
    // Assuming we have a service to get current active period
    // private final AcademicPeriodService periodService;

    private static final int ABSENCE_THRESHOLD = 3; // Number of absences to trigger alert
    private static final int WARNING_PERCENTAGE = 15; // Percentage of total days

    public AttendanceAlertJob(AttendanceRepository attendanceRepository,
            NotificationRepository notificationRepository) {
        this.attendanceRepository = attendanceRepository;
        this.notificationRepository = notificationRepository;
    }

    // Run every day at 6:00 PM
    @Scheduled(cron = "0 0 18 * * ?")
    @Transactional
    public void checkAttendanceAndNotify() {
        // This is a simplified logic. In a real scenario, we would iterate active
        // students
        // and check their absence count for the current period.

        // For demonstration, let's say we fetch students who were absent TODAY
        LocalDate today = LocalDate.now();
        List<Attendance> absencesToday = attendanceRepository.findByDateAndStatus(today, AttendanceStatus.ABSENT);

        for (Attendance attendance : absencesToday) {
            // Count total absences for this student in the current month/period
            long totalAbsences = attendanceRepository.countByStudentIdAndStatusAndDateAfter(
                    attendance.getStudent().getId(),
                    AttendanceStatus.ABSENT,
                    today.minusMonths(1) // Check last month window
            );

            if (totalAbsences >= ABSENCE_THRESHOLD) {
                createAlert(attendance, totalAbsences);
            }
        }
    }

    private void createAlert(Attendance attendance, long totalAbsences) {
        if (attendance.getStudent().getUser() == null) {
            // Cannot notify if no user linked
            return;
        }

        Notification notification = new Notification();
        notification.setTitle("Alerta de Asistencia");
        notification.setMessage("El estudiante " + attendance.getStudent().getFullName() +
                " ha acumulado " + totalAbsences + " inasistencias en el último mes.");
        notification.setUser(attendance.getStudent().getUser());
        notification.setType(NotificationType.ALERT);
        notification.setPriority(com.school.communication.enums.Priority.HIGH);
        notification.setRead(false);
        notification.setCreatedAt(java.time.LocalDateTime.now());

        notificationRepository.save(notification);
    }
}
