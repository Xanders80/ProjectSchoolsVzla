package com.school.web.controller;

import com.school.core.entity.User;
import com.school.core.repository.UserRepository;
import com.school.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        
        // Cargar entidad específica según rol
        switch (user.getRole()) {
            case PARENT:
                parentService.getAllParents(org.springframework.data.domain.PageRequest.of(0, 1))
                    .getContent().stream()
                    .filter(p -> p.getUser() != null && p.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .ifPresent(parent -> model.addAttribute("parentInfo", parent));
                break;
            case TEACHER:
            case STAFF:
                staffService.getAllStaff(org.springframework.data.domain.PageRequest.of(0, 1000))
                    .getContent().stream()
                    .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .ifPresent(staff -> model.addAttribute("staffInfo", staff));
                break;
            case STUDENT:
                academicService.getAllStudents(org.springframework.data.domain.PageRequest.of(0, 1000))
                    .getContent().stream()
                    .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .ifPresent(student -> model.addAttribute("studentInfo", student));
                break;
            default:
                break;
        }
        
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String relationship,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String specialization,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualizar usuario base
            userService.updateUserProfile(user, firstName, lastName, email);
            
            // Actualizar entidad específica
            updateSpecificEntity(user, dni, phoneNumber, address, relationship, department, specialization);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }
    
    private void updateSpecificEntity(User user, String dni, String phoneNumber, String address, 
            String relationship, String department, String specialization) {
        switch (user.getRole()) {
            case PARENT:
                parentService.getAllParents(org.springframework.data.domain.PageRequest.of(0, 1000))
                    .getContent().stream()
                    .filter(p -> p.getUser() != null && p.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .ifPresent(parent -> {
                        parent.setFirstName(user.getFirstName());
                        parent.setLastName(user.getLastName());
                        parent.setEmail(user.getEmail());
                        if (dni != null) parent.setDni(dni);
                        if (phoneNumber != null) parent.setPhoneNumber(phoneNumber);
                        if (address != null) parent.setAddress(address);
                        if (relationship != null) parent.setRelationship(relationship);
                        parentService.saveParent(parent);
                    });
                break;
            case TEACHER:
            case STAFF:
                staffService.getAllStaff(org.springframework.data.domain.PageRequest.of(0, 1000))
                    .getContent().stream()
                    .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .ifPresent(staff -> {
                        staff.setFirstName(user.getFirstName());
                        staff.setLastName(user.getLastName());
                        staff.setEmail(user.getEmail());
                        if (dni != null) staff.setDni(dni);
                        if (phoneNumber != null) staff.setPhoneNumber(phoneNumber);
                        if (address != null) staff.setAddress(address);
                        if (department != null) staff.setDepartment(department);
                        if (specialization != null) staff.setSpecialization(specialization);
                        staffService.saveStaff(staff);
                    });
                break;
            case STUDENT:
                academicService.getAllStudents(org.springframework.data.domain.PageRequest.of(0, 1000))
                    .getContent().stream()
                    .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .ifPresent(student -> {
                        student.setFirstName(user.getFirstName());
                        student.setLastName(user.getLastName());
                        student.setEmail(user.getEmail());
                        if (dni != null) student.setDni(dni);
                        if (phoneNumber != null) student.setPhoneNumber(phoneNumber);
                        if (address != null) student.setAddress(address);
                        academicService.saveStudent(student);
                    });
                break;
            default:
                break;
        }
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
