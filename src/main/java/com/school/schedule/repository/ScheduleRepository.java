package com.school.schedule.repository;

import com.school.schedule.entity.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntry, Long> {

    List<ScheduleEntry> findBySectionId(Long sectionId);

    // Conflict detection: Overlapping times for same Room
    @Query("SELECT s FROM ScheduleEntry s JOIN s.section sec WHERE sec.room.id = :roomId AND s.dayOfWeek = :day AND " +
           "((s.startTime < :end AND s.endTime > :start))")
    List<ScheduleEntry> findConflictsByRoom(@Param("roomId") Long roomId,
                                            @Param("day") DayOfWeek day,
                                            @Param("start") LocalTime start,
                                            @Param("end") LocalTime end);

    // Conflict detection: Overlapping times for same Teacher
    @Query("SELECT s FROM ScheduleEntry s JOIN s.section sec WHERE sec.teacher.id = :teacherId AND s.dayOfWeek = :day AND " +
           "((s.startTime < :end AND s.endTime > :start))")
    List<ScheduleEntry> findConflictsByTeacher(@Param("teacherId") Long teacherId,
                                               @Param("day") DayOfWeek day,
                                               @Param("start") LocalTime start,
                                               @Param("end") LocalTime end);
}
