package com.school.web.controller.infra;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.admin.entity.Staff;
import com.school.admin.repository.StaffRepository;
import com.school.infra.entity.LabReservation;
import com.school.infra.entity.Room;
import com.school.infra.enums.ReservationStatus;
import com.school.infra.service.LabReservationService;

@Controller
@RequestMapping("/infra/labs")
public class LabReservationController {

    private static final String MSG_SUCCESS = "successMessage";
    private static final String MSG_ERROR = "errorMessage";

    private final LabReservationService reservationService;
    private final StaffRepository staffRepository;

    public LabReservationController(LabReservationService reservationService, StaffRepository staffRepository) {
        this.reservationService = reservationService;
        this.staffRepository = staffRepository;
    }

    @GetMapping("/reservations")
    public String listRooms(Model model) {
        model.addAttribute("labRooms", reservationService.getLabRooms());
        return "infra/lab-list";
    }

    @GetMapping("/reservations/all")
    public String listAllReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "infra/reservation-list";
    }

    @GetMapping("/management")
    public String showManagementView(Model model) {
        model.addAttribute("pendingReservations", reservationService.getPendingReservations());
        model.addAttribute("approvedReservations",
                reservationService.getReservationsByStatus(ReservationStatus.APPROVED));
        model.addAttribute("rejectedReservations",
                reservationService.getReservationsByStatus(ReservationStatus.REJECTED));
        return "infra/reservation-management";
    }

    @GetMapping("/reserve/{roomId}")
    public String showReservationForm(@PathVariable @NonNull Long roomId, Model model) {
        Room room = reservationService.getRoomById(roomId);
        model.addAttribute("room", room);
        model.addAttribute("reservation", new LabReservation());
        model.addAttribute("existingReservations", reservationService.getReservationsByRoom(room));
        model.addAttribute("teachers", staffRepository.findAll().stream()
                .filter(s -> "TEACHER".equals(s.getJobTitle().name()))
                .toList());
        return "infra/lab-reserve-form";
    }

    @PostMapping("/reserve")
    public String saveReservation(LabReservation reservation, RedirectAttributes redirectAttributes) {
        try {
            reservationService.createReservation(reservation);
            redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Reserva creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(MSG_ERROR, "Error al crear reserva: " + e.getMessage());
            return "redirect:/infra/labs/reserve/" + reservation.getRoom().getId();
        }
        return "redirect:/infra/labs/reservations";
    }

    @PostMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Reserva cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(MSG_ERROR, "Error al cancelar reserva: " + e.getMessage());
        }
        return "redirect:/infra/labs/reservations/all";
    }

    @PostMapping("/approve/{id}")
    public String approveReservation(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario actual desde el contexto de seguridad
            String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            Staff approver = staffRepository.findAll().stream()
                    .filter(s -> "ADMIN".equals(s.getJobTitle().name()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No se encontró un administrador"));

            reservationService.approveReservation(id, approver);
            redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Reserva aprobada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(MSG_ERROR, "Error al aprobar reserva: " + e.getMessage());
        }
        return "redirect:/infra/labs/management";
    }

    @PostMapping("/reject/{id}")
    public String rejectReservation(@PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario actual desde el contexto de seguridad
            String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            Staff rejector = staffRepository.findAll().stream()
                    .filter(s -> "ADMIN".equals(s.getJobTitle().name()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No se encontró un administrador"));

            reservationService.rejectReservation(
                    java.util.Objects.requireNonNull(id, "ID de reserva no puede ser null"), rejector, reason);
            redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Reserva rechazada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(MSG_ERROR, "Error al rechazar reserva: " + e.getMessage());
        }
        return "redirect:/infra/labs/management";
    }
}
