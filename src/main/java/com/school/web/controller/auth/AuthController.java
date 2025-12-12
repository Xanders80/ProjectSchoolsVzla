package com.school.web.controller.auth;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.school.core.service.UserService userService;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/register?error";
        }

        try {
            userService.registerNewUser(firstName, lastName, email, username, password);
            redirectAttributes.addFlashAttribute("success", "Cuenta creada exitosamente. Por favor inicie sesión.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register?error";
        }
    }

    @GetMapping("/404")
    public String show404Page() {
        return "error/404";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        java.util.Optional<com.school.core.entity.User> userOptional = userService.findByEmail(email);

        if (!userOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró ninguna cuenta con ese correo electrónico.");
            return "redirect:/forgot-password?error";
        }

        com.school.core.entity.User user = userOptional.get();
        String token = java.util.UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        // Simulación de envío de correo
        System.out.println("--------------------------------------------------------------");
        System.out.println("SIMULACIÓN DE EMAIL DE RECUPERACIÓN");
        System.out.println("Para: " + email);
        System.out.println("Token: " + token);
        System.out.println("Enlace: http://localhost:8080/reset-password?token=" + token);
        System.out.println("--------------------------------------------------------------");

        redirectAttributes.addFlashAttribute("success",
                "Se ha enviado un enlace de recuperación a su correo electrónico (Revise la consola del servidor).");
        return "redirect:/forgot-password?success";
    }
}
