package com.school.academic.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.school.academic.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySectionIdAndDate(Long sectionId, LocalDate date);

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByDateAndStatus(java.time.LocalDate date, com.school.academic.enums.AttendanceStatus status);

    long countByStudentIdAndStatusAndDateAfter(Long studentId, com.school.academic.enums.AttendanceStatus status,
            java.time.LocalDate date);

    @Modifying
    void deleteByStudentId(Long studentId);

    boolean existsBySectionId(Long sectionId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Attendance a WHERE a.section.id = ?1")
    long countBySectionId(Long sectionId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = ?1 AND a.status = 'ABSENT' AND a.date BETWEEN ?2 AND ?3")
    long countAbsencesByStudentAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Attendance a WHERE a.date BETWEEN ?1 AND ?2")
    long countByDateBetween(LocalDate startDate, LocalDate endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Attendance a WHERE a.date BETWEEN ?1 AND ?2 AND a.status = ?3")
    long countByDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, com.school.academic.enums.AttendanceStatus status);
}
