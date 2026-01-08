package com.school.academic.job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Student;
import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.StudentRepository;
import com.school.communication.enums.NotificationType;
import com.school.communication.service.NotificationService;

@Component
public class AttendanceAlertJob {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public AttendanceAlertJob(AttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            NotificationService notificationService) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
    }

    // Run at 6:00 PM every weekday to catch absences recorded during the day
    @Scheduled(cron = "0 0 18 * * MON-FRI")
    @Transactional
    public void checkAttendanceAlerts() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7); // Last 7 days window
        long threshold = 3;

        // Optimized: only fetch students who actually exceed the threshold
        List<Student> excessiveAbsenceStudents = studentRepository.findStudentsWithAbsencesMoreThan(startDate, endDate,
                threshold);

        for (Student student : excessiveAbsenceStudents) {
            // Double check count to be precise for the message (though query guarantees >=
            // threshold)
            long absences = attendanceRepository.countAbsencesByStudentAndDateRange(
                    student.getId(), startDate, endDate);

            createAttendanceAlert(student, absences);
        }
    }

    private void createAttendanceAlert(Student student, long absences) {
        String message = String.format(
                "ALERTA: El estudiante %s %s ha acumulado %d inasistencias en los últimos 7 días.",
                student.getFirstName(), student.getLastName(), absences);

        // Only create notification if user is linked
        if (student.getUser() != null) {
            notificationService.createNotification(
                    "Alerta de Asistencia Crítica",
                    message,
                    NotificationType.ATTENDANCE_ALERT,
                    student.getUser());
        }
    }
}