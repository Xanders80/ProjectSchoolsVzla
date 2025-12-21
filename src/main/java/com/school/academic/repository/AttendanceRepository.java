package com.school.academic.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.school.academic.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySectionIdAndDate(Long sectionId, LocalDate date);

    List<Attendance> findByStudentId(Long studentId);

    @Modifying
    void deleteByStudentId(Long studentId);
    
    boolean existsBySectionId(Long sectionId);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Attendance a WHERE a.section.id = ?1")
    long countBySectionId(Long sectionId);
}
