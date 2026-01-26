package com.school.academic.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Attendance;
import com.school.academic.entity.Section;
import com.school.academic.entity.Student;
import com.school.academic.enums.AttendanceStatus;
import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.EnrollmentRepository;
import com.school.academic.repository.SectionRepository;
import com.school.academic.repository.StudentRepository;
import com.school.academic.dto.AttendanceDTO;
import com.school.academic.entity.Enrollment;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, SectionRepository sectionRepository,
            StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public void saveBatchAttendance(@NonNull Long sectionId, LocalDate date,
            Map<Long, AttendanceStatus> studentStatuses,
            Map<Long, String> studentRemarks) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Sección no encontrada"));

        List<Attendance> existingRecords = attendanceRepository.findBySectionIdAndDate(sectionId, date);
        Map<Long, Attendance> existingMap = new HashMap<>();
        for (Attendance a : existingRecords) {
            existingMap.put(a.getStudent().getId(), a);
        }

        for (Map.Entry<Long, AttendanceStatus> entry : studentStatuses.entrySet()) {
            Long studentId = entry.getKey();
            AttendanceStatus status = entry.getValue();
            String remarks = studentRemarks.getOrDefault(studentId, "");

            Attendance attendance = existingMap.get(studentId);
            if (attendance == null) {
                attendance = new Attendance();
                @SuppressWarnings("null")
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado: " + studentId));
                attendance.setStudent(student);
                attendance.setSection(section);
                attendance.setDate(date);
            }
            attendance.setStatus(status);
            attendance.setRemarks(remarks);
            attendanceRepository.save(attendance);
        }
    }

    public List<AttendanceDTO> getAttendanceDTOs(Long sectionId, LocalDate date) {
        List<Enrollment> enrollments = enrollmentRepository.findBySectionIdAndStudentDeletedFalse(sectionId);
        List<Attendance> currentAttendance = attendanceRepository.findBySectionIdAndDate(sectionId, date);
        Map<Long, Attendance> attendanceMap = new HashMap<>();
        for (Attendance a : currentAttendance) {
            attendanceMap.put(a.getStudent().getId(), a);
        }

        List<AttendanceDTO> dtos = new java.util.ArrayList<>();
        for (Enrollment e : enrollments) {
            Attendance a = attendanceMap.get(e.getStudent().getId());
            if (a != null) {
                dtos.add(new AttendanceDTO(e.getStudent(), a.getStatus(), a.getId(), a.getRemarks()));
            } else {
                dtos.add(new AttendanceDTO(e.getStudent(), AttendanceStatus.PRESENT, null, null));
            }
        }
        return dtos;
    }

    public List<Attendance> getAttendanceBySectionAndDate(Long sectionId, LocalDate date) {
        return attendanceRepository.findBySectionIdAndDate(sectionId, date);
    }

    public Map<String, Long> getMonthlyStats(Long studentId, int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Object[]> results = attendanceRepository.countStatusByStudentAndDateRange(studentId, startDate, endDate);
        Map<String, Long> stats = new HashMap<>();
        for (Object[] row : results) {
            stats.put(((AttendanceStatus) row[0]).name(), (Long) row[1]);
        }
        return stats;
    }

    public boolean checkAttendanceThreshold(Long studentId) {
        // Simple logic: Check last 30 days. If absent > 5 times, return false (alert
        // needed)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        long absences = attendanceRepository.countAbsencesByStudentAndDateRange(studentId, startDate, endDate);
        return absences < 5; // Returns true if good, false if alert needed
    }

    public List<com.school.academic.dto.StudentAttendanceStatsDTO> getSectionStats(Long sectionId, int month,
            int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Enrollment> enrollments = enrollmentRepository.findBySectionIdAndStudentDeletedFalse(sectionId);
        List<com.school.academic.dto.StudentAttendanceStatsDTO> stats = new java.util.ArrayList<>();

        for (Enrollment e : enrollments) {
            Long studentId = e.getStudent().getId();
            List<Object[]> results = attendanceRepository.countStatusByStudentAndDateRange(studentId, startDate,
                    endDate);

            long present = 0;
            long late = 0;
            long absent = 0;
            long excused = 0;

            for (Object[] row : results) {
                AttendanceStatus status = (AttendanceStatus) row[0];
                Long count = (Long) row[1];
                switch (status) {
                    case PRESENT -> present = count;
                    case LATE -> late = count;
                    case ABSENT -> absent = count;
                    case EXCUSED -> excused = count;
                }
            }
            // Logic: we want student name too
            String studentName = e.getStudent().getFirstName() + " " + e.getStudent().getLastName();
            stats.add(new com.school.academic.dto.StudentAttendanceStatsDTO(studentName, studentId, present, late,
                    absent, excused));
        }
        return stats;
    }
}
