package com.school.infra.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.infra.dto.LabStatisticsDTO;
import com.school.infra.dto.TeacherUsageDTO;
import com.school.infra.entity.LabReservation;
import com.school.infra.entity.Room;
import com.school.infra.enums.ReservationStatus;
import com.school.infra.repository.LabReservationRepository;
import com.school.infra.repository.RoomRepository;

@Service
public class LabStatisticsService {

    private final LabReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public LabStatisticsService(LabReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<LabStatisticsDTO> getStatisticsByDateRange(LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.atTime(23, 59, 59);

        List<Room> labRooms = roomRepository.findByDeletedFalse().stream()
                .filter(r -> "LAB".equalsIgnoreCase(r.getType()) || r.getRoomNumber().contains("LAB"))
                .toList();

        List<LabStatisticsDTO> statistics = new ArrayList<>();

        for (Room room : labRooms) {
            // Use optimized query instead of findAll() + filter
            List<LabReservation> reservations = reservationRepository.findByRoomAndDateRange(
                    room.getId(), startDateTime, endDateTime);

            long total = reservations.size();
            long approved = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.APPROVED).count();
            long rejected = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.REJECTED).count();
            long pending = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.PENDING).count();

            long totalHours = reservations.stream()
                    .filter(r -> r.getStatus() == ReservationStatus.APPROVED)
                    .mapToLong(r -> Duration.between(r.getStartTime(), r.getEndTime()).toHours())
                    .sum();

            long daysInPeriod = Duration.between(startDateTime, endDateTime).toDays();
            long availableHours = calculateAvailableHours(daysInPeriod);
            double occupancyRate = availableHours > 0 ? (totalHours * 100.0 / availableHours) : 0.0;

            LabStatisticsDTO dto = new LabStatisticsDTO(
                    room.getRoomNumber(),
                    total,
                    approved,
                    rejected,
                    pending,
                    Math.round(occupancyRate * 100.0) / 100.0,
                    totalHours);

            statistics.add(dto);
        }

        return statistics;
    }

    @Transactional(readOnly = true)
    public List<TeacherUsageDTO> getTopTeachersByUsage(int limit, LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.atTime(23, 59, 59);

        // Use optimized query
        List<LabReservation> reservations = reservationRepository.findByDateRange(startDateTime, endDateTime);

        Map<String, TeacherUsageDTO> teacherMap = new HashMap<>();

        for (LabReservation reservation : reservations) {
            String teacherKey = reservation.getTeacher().getDni();
            String teacherName = reservation.getTeacher().getFirstName() + " " +
                    reservation.getTeacher().getLastName();

            TeacherUsageDTO dto = teacherMap.getOrDefault(teacherKey,
                    new TeacherUsageDTO(teacherName, teacherKey, 0L, 0L, 0L));

            dto.setReservationCount(dto.getReservationCount() + 1);

            if (reservation.getStatus() == ReservationStatus.APPROVED) {
                dto.setApprovedCount(dto.getApprovedCount() + 1);
                long hours = Duration.between(reservation.getStartTime(), reservation.getEndTime()).toHours();
                dto.setTotalHours(dto.getTotalHours() + hours);
            }

            teacherMap.put(teacherKey, dto);
        }

        return teacherMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getReservationCount(), a.getReservationCount()))
                .limit(limit)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getReservationsByStatus(LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.atTime(23, 59, 59);

        // Use optimized query
        List<LabReservation> reservations = reservationRepository.findByDateRange(startDateTime, endDateTime);
        Map<String, Long> statusCount = new HashMap<>();

        statusCount.put("PENDING",
                reservations.stream().filter(r -> r.getStatus() == ReservationStatus.PENDING).count());
        statusCount.put("APPROVED",
                reservations.stream().filter(r -> r.getStatus() == ReservationStatus.APPROVED).count());
        statusCount.put("REJECTED",
                reservations.stream().filter(r -> r.getStatus() == ReservationStatus.REJECTED).count());
        statusCount.put("CANCELLED",
                reservations.stream().filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count());

        return statusCount;
    }

    @Transactional(readOnly = true)
    public Map<Integer, Long> getPeakHoursStatistics(LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.atTime(23, 59, 59);

        // Use optimized query
        List<LabReservation> approved = reservationRepository.findByStatusAndDateRange(
                ReservationStatus.APPROVED, startDateTime, endDateTime);
        Map<Integer, Long> hourCount = new HashMap<>();

        for (int hour = 0; hour < 24; hour++) {
            hourCount.put(hour, 0L);
        }

        for (LabReservation reservation : approved) {
            int startHour = reservation.getStartTime().getHour();
            hourCount.put(startHour, hourCount.get(startHour) + 1);
        }

        return hourCount;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getGeneralStatistics(LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.atTime(23, 59, 59);

        // Use optimized query
        List<LabReservation> reservations = reservationRepository.findByDateRange(startDateTime, endDateTime);
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalReservations", (long) reservations.size());
        stats.put("approvedReservations",
                reservations.stream().filter(r -> r.getStatus() == ReservationStatus.APPROVED).count());
        stats.put("pendingReservations",
                reservations.stream().filter(r -> r.getStatus() == ReservationStatus.PENDING).count());
        stats.put("rejectedReservations",
                reservations.stream().filter(r -> r.getStatus() == ReservationStatus.REJECTED).count());

        long approvedCount = (long) stats.get("approvedReservations");
        long totalCount = (long) stats.get("totalReservations");
        double approvalRate = totalCount > 0 ? (approvedCount * 100.0 / totalCount) : 0.0;
        stats.put("approvalRate", Math.round(approvalRate * 100.0) / 100.0);

        long totalHours = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.APPROVED)
                .mapToLong(r -> Duration.between(r.getStartTime(), r.getEndTime()).toHours())
                .sum();
        stats.put("totalHoursReserved", totalHours);

        double avgHours = approvedCount > 0 ? (totalHours * 1.0 / approvedCount) : 0.0;
        stats.put("averageHoursPerReservation", Math.round(avgHours * 100.0) / 100.0);

        return stats;
    }

    private long calculateAvailableHours(long days) {
        // Monday-Friday: 13 hours (7:00-20:00)
        // Saturday: 6 hours (8:00-14:00)
        // Sunday: 0 hours
        long weeks = days / 7;
        long remainingDays = days % 7;

        long weekdayHours = 13L * 5; // 65 hours per week (Mon-Fri)
        long saturdayHours = 6L; // 6 hours on Saturday

        long totalWeekHours = weekdayHours + saturdayHours; // 71 hours per week
        long baseHours = weeks * totalWeekHours;

        // Add remaining days
        for (int i = 0; i < remainingDays; i++) {
            DayOfWeek day = DayOfWeek.of((i % 7) + 1);
            if (day == DayOfWeek.SATURDAY) {
                baseHours += 6;
            } else if (day != DayOfWeek.SUNDAY) {
                baseHours += 13;
            }
        }

        return baseHours;
    }
}
