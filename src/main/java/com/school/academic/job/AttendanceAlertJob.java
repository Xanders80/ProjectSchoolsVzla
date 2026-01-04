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

    @Scheduled(cron = "0 0 8 * * MON-FRI") // 8 AM, weekdays
    @Transactional
    public void checkAttendanceAlerts() {
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        List<Student> students = studentRepository.findAllActive();

        for (Student student : students) {
            long absences = attendanceRepository.countAbsencesByStudentAndDateRange(
                    student.getId(), weekAgo, LocalDate.now());

            if (absences >= 3) {
                createAttendanceAlert(student, absences);
            }
        }
    }

    private void createAttendanceAlert(Student student, long absences) {
        String message = String.format("Estudiante %s %s tiene %d inasistencias en la última semana",
                student.getFirstName(), student.getLastName(), absences);

        notificationService.createNotification(
                "Alerta de Asistencia",
                message,
                NotificationType.ATTENDANCE_ALERT,
                student.getUser());
    }
}