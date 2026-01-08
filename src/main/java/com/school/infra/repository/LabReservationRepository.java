package com.school.infra.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.school.infra.entity.LabReservation;
import com.school.infra.entity.Room;
import com.school.infra.enums.ReservationStatus;

@Repository
public interface LabReservationRepository extends JpaRepository<LabReservation, Long> {
        List<LabReservation> findByRoomAndStatus(Room room, ReservationStatus status);

        List<LabReservation> findByStatus(ReservationStatus status);

        @Query("SELECT r FROM LabReservation r WHERE r.room = :room " +
                        "AND r.status = 'APPROVED' " +
                        "AND ((r.startTime < :end AND r.endTime > :start))")
        List<LabReservation> findOverlappingReservations(@Param("room") Room room,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        // Optimized queries for date range filtering
        @Query("SELECT r FROM LabReservation r WHERE r.startTime BETWEEN :startDateTime AND :endDateTime")
        List<LabReservation> findByDateRange(
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime);

        @Query("SELECT r FROM LabReservation r WHERE r.room.id = :roomId AND r.startTime BETWEEN :startDateTime AND :endDateTime")
        List<LabReservation> findByRoomAndDateRange(
                        @Param("roomId") Long roomId,
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime);

        @Query("SELECT r FROM LabReservation r WHERE r.status = :status AND r.startTime BETWEEN :startDateTime AND :endDateTime")
        List<LabReservation> findByStatusAndDateRange(
                        @Param("status") ReservationStatus status,
                        @Param("startDateTime") LocalDateTime startDateTime,
                        @Param("endDateTime") LocalDateTime endDateTime);
}
