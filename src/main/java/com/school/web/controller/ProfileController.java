package com.school.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.core.entity.User;
import com.school.core.repository.UserRepository;
import com.school.core.service.UserService;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.school.core.service.ParentService parentService;

    @Autowired
    private com.school.admin.service.StaffService staffService;

    @Autowired
    private com.school.academic.service.AcademicService academicService;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("user", user);

        // Cargar entidad específica según rol de forma eficiente
        java.time.LocalDate birthDate = null;
        switch (user.getRole()) {
            case PARENT:
                var parent = parentService.getParentByUserId(user.getId()).orElse(null);
                if (parent != null) {
                    model.addAttribute("parentInfo", parent);
                    birthDate = parent.getBirthDate();
                }
                break;
            case ADMIN:
            case DIRECTOR:
            case TEACHER:
            case STAFF:
                var staff = staffService.getStaffByUserId(user.getId()).orElse(null);
                if (staff != null) {
                    model.addAttribute("staffInfo", staff);
                    birthDate = staff.getBirthDate();
                }
                break;
            case STUDENT:
                var student = academicService.getStudentByUserId(user.getId()).orElse(null);
                if (student != null) {
                    model.addAttribute("studentInfo", student);
                    birthDate = student.getBirthDate();
                }
                break;
            default:
                break;
        }

        // Datos adicionales para "Detalles de la Cuenta"
        if (birthDate != null) {
            model.addAttribute("age", java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears());
        }
        model.addAttribute("registrationDate", user.getCreatedAt());
        model.addAttribute("accountStatus", user.isEnabled() ? "Activa" : "Inactiva");
        model.addAttribute("statusColor", user.isEnabled() ? "success" : "danger");

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String relationship,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate birthDate,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualizar perfil de forma centralizada a través del servicio
            userService.updateUserProfile(user, firstName, lastName, email, username, dni, phoneNumber, address,
                    relationship,
                    department, specialization, birthDate);

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping("/settings")
    public String showSettings(Model model) {
        return "settings";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las nuevas contraseñas no coinciden");
            return "redirect:/settings";
        }

        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!userService.checkIfValidOldPassword(user, currentPassword)) {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/settings";
            }

            userService.changeUserPassword(user, newPassword);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña");
        }

        return "redirect:/settings";
    }
}
