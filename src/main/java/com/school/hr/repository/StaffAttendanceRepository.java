package com.school.hr.repository;

import com.school.hr.entity.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance, Long> {
    List<StaffAttendance> findByDate(LocalDate date);

    Optional<StaffAttendance> findByStaffIdAndDate(Long staffId, LocalDate date);
}
