package com.school.schedule.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Section;
import com.school.academic.repository.SectionRepository;
import com.school.schedule.entity.ScheduleEntry;
import com.school.schedule.repository.ScheduleRepository;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SectionRepository sectionRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, SectionRepository sectionRepository) {
        this.scheduleRepository = scheduleRepository;
        this.sectionRepository = sectionRepository;
    }

    public List<ScheduleEntry> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public ScheduleEntry saveSchedule(ScheduleEntry entry) {
        // Validate conflicts before saving
        // Note: For Update operations, we should exclude the current entry ID from
        // conflict check
        // Simplified for MVP: Check strictly against DB

        Long sectionId = Objects.requireNonNull(entry.getSection().getId(), "Section ID cannot be null");
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Section ID"));

        // 1. Room Conflict
        if (section.getRoom() != null) {
            List<ScheduleEntry> roomConflicts = scheduleRepository.findConflictsByRoom(
                    section.getRoom().getId(),
                    entry.getDayOfWeek(),
                    entry.getStartTime(),
                    entry.getEndTime());
            if (!roomConflicts.isEmpty()) {
                // Ignore self if editing
                if (entry.getId() == null || roomConflicts.stream().anyMatch(c -> !c.getId().equals(entry.getId()))) {
                    throw new IllegalStateException("Room is already booked for this time slot.");
                }
            }
        }

        // 2. Teacher Conflict
        if (section.getTeacher() != null) {
            List<ScheduleEntry> teacherConflicts = scheduleRepository.findConflictsByTeacher(
                    section.getTeacher().getId(),
                    entry.getDayOfWeek(),
                    entry.getStartTime(),
                    entry.getEndTime());
            if (!teacherConflicts.isEmpty()) {
                if (entry.getId() == null
                        || teacherConflicts.stream().anyMatch(c -> !c.getId().equals(entry.getId()))) {
                    throw new IllegalStateException("Teacher is already booked for this time slot.");
                }
            }
        }

        return scheduleRepository.save(entry);
    }

    public void deleteSchedule(@NonNull Long id) {
        scheduleRepository.deleteById(id);
    }

    public Optional<ScheduleEntry> getScheduleById(@NonNull Long id) {
        return scheduleRepository.findById(id);
    }
}
