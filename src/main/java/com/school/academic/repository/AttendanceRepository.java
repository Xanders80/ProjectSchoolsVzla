package com.school.academic.repository;

import com.school.academic.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySectionIdAndDate(Long sectionId, LocalDate date);

    List<Attendance> findByStudentId(Long studentId);
}
