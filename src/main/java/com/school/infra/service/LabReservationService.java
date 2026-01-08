package com.school.infra.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.admin.entity.Staff;
import com.school.communication.enums.NotificationType;
import com.school.communication.service.EmailService;
import com.school.communication.service.NotificationService;
import com.school.infra.entity.LabReservation;
import com.school.infra.entity.Room;
import com.school.infra.enums.ReservationStatus;
import com.school.infra.repository.LabReservationRepository;
import com.school.infra.repository.RoomRepository;

@Service
public class LabReservationService {

    private final LabReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public LabReservationService(LabReservationRepository reservationRepository,
            RoomRepository roomRepository,
            NotificationService notificationService,
            EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<Room> getLabRooms() {
        return roomRepository.findByDeletedFalse().stream()
                .filter(r -> "LAB".equalsIgnoreCase(r.getType()) || r.getRoomNumber().contains("LAB"))
                .toList();
    }

    @Transactional(readOnly = true)
    public Room getRoomById(@NonNull Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<LabReservation> getReservationsByRoom(Room room) {
        return reservationRepository.findByRoomAndStatus(room, ReservationStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public List<LabReservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<LabReservation> getPendingReservations() {
        return reservationRepository.findByStatus(ReservationStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<LabReservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    @Transactional
    public LabReservation createReservation(LabReservation reservation) {
        // Validate business rules
        validateReservation(reservation);

        // Validate overlap
        List<LabReservation> overlaps = reservationRepository.findOverlappingReservations(
                reservation.getRoom(),
                reservation.getStartTime(),
                reservation.getEndTime());

        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("El laboratorio ya está reservado en ese horario.");
        }

        // Set initial status
        reservation.setStatus(ReservationStatus.PENDING);
        LabReservation saved = reservationRepository.save(reservation);

        // Trigger notification
        String msg = String.format("Nueva reserva para el laboratorio %s por el docente %s %s para las %s",
                reservation.getRoom().getRoomNumber(),
                reservation.getTeacher().getFirstName(),
                reservation.getTeacher().getLastName(),
                reservation.getStartTime().toString());

        notificationService.createNotification("Reserva de Laboratorio", msg, NotificationType.INFO,
                reservation.getTeacher().getUser());

        // Send email notification
        emailService.sendReservationCreatedEmail(saved);

        return saved;
    }

    @Transactional
    public void approveReservation(@NonNull Long id, Staff approver) {
        LabReservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden aprobar reservas pendientes");
        }

        reservation.setStatus(ReservationStatus.APPROVED);
        reservation.setApprovedBy(approver);
        reservation.setApprovedAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        // Notify teacher
        String msg = String.format("Su reserva para el laboratorio %s ha sido APROBADA",
                reservation.getRoom().getRoomNumber());
        notificationService.createNotification("Reserva Aprobada", msg, NotificationType.INFO,
                reservation.getTeacher().getUser());

        // Send email notification
        emailService.sendReservationApprovedEmail(reservation);
    }

    @Transactional
    public void rejectReservation(@NonNull Long id, Staff rejector, String reason) {
        LabReservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden rechazar reservas pendientes");
        }

        reservation.setStatus(ReservationStatus.REJECTED);
        reservation.setApprovedBy(rejector);
        reservation.setApprovedAt(LocalDateTime.now());
        reservation.setRejectionReason(reason);
        reservationRepository.save(reservation);

        // Notify teacher
        String msg = String.format("Su reserva para el laboratorio %s ha sido RECHAZADA. Motivo: %s",
                reservation.getRoom().getRoomNumber(), reason);
        notificationService.createNotification("Reserva Rechazada", msg, NotificationType.WARNING,
                reservation.getTeacher().getUser());

        // Send email notification
        emailService.sendReservationRejectedEmail(reservation);
    }

    private void validateReservation(LabReservation reservation) {
        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime();

        // Validate not in the past
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden crear reservas en fechas pasadas");
        }

        // Validate minimum advance (24 hours)
        if (start.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("Las reservas deben hacerse con al menos 24 horas de anticipación");
        }

        // Validate end after start
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        // Validate duration
        Duration duration = Duration.between(start, end);
        if (duration.toMinutes() < 30) {
            throw new IllegalArgumentException("La duración mínima de una reserva es de 30 minutos");
        } else if (duration.toHours() > 4) {
            throw new IllegalArgumentException("La duración máxima de una reserva es de 4 horas");
        }

        // Validate working hours
        validateWorkingHours(start, end);

        // Validate capacity
        if (reservation.getNumberOfStudents() != null
                && reservation.getRoom().getCapacity() != null
                && reservation.getNumberOfStudents() > reservation.getRoom().getCapacity()) {
            throw new IllegalArgumentException(
                    String.format("El número de estudiantes (%d) excede la capacidad del laboratorio (%d)",
                            reservation.getNumberOfStudents(), reservation.getRoom().getCapacity()));
        }
    }

    private void validateWorkingHours(LocalDateTime start, LocalDateTime end) {
        DayOfWeek startDay = start.getDayOfWeek();
        DayOfWeek endDay = end.getDayOfWeek();

        // No reservations on Sundays
        if (startDay == DayOfWeek.SUNDAY || endDay == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No se permiten reservas los domingos");
        }

        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        // Monday to Friday: 7:00 - 20:00
        if (startDay != DayOfWeek.SATURDAY
                && (startTime.isBefore(LocalTime.of(7, 0)) || endTime.isAfter(LocalTime.of(20, 0)))) {
            throw new IllegalArgumentException(
                    "Las reservas de lunes a viernes deben estar entre las 7:00 y las 20:00");
        }

        // Saturday: 8:00 - 14:00
        if (startDay == DayOfWeek.SATURDAY
                && (startTime.isBefore(LocalTime.of(8, 0)) || endTime.isAfter(LocalTime.of(14, 0)))) {
            throw new IllegalArgumentException("Las reservas los sábados deben estar entre las 8:00 y las 14:00");
        }
    }

    @Transactional
    public void cancelReservation(@NonNull Long id) {
        reservationRepository.deleteById(id);
    }
}
