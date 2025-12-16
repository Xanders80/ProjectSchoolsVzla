package com.school.web.controller.communication;

import com.school.communication.enums.NotificationType;
import com.school.communication.service.CommunicationService;
import com.school.core.entity.User;
import com.school.core.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final CommunicationService communicationService;
    private final UserService userService;

    public NotificationController(CommunicationService communicationService, UserService userService) {
        this.communicationService = communicationService;
        this.userService = userService;
    }

    @GetMapping
    public String listNotifications(Model model, @PageableDefault(size = 10) Pageable pageable) {
        User user = getLoggedUser();
        model.addAttribute("notifications", communicationService.getUserNotifications(user.getId(), pageable));
        return "communication/notification-list";
    }

    @GetMapping("/broadcast")
    // Security handled in SecurityConfig or Method Security
    public String broadcastForm(Model model) {
        return "communication/broadcast-form";
    }

    @PostMapping("/broadcast")
    public String sendBroadcast(
            @RequestParam NotificationType type,
            @RequestParam String message,
            RedirectAttributes redirectAttributes) {
        try {
            communicationService.broadcastNotification(type, message);
            redirectAttributes.addFlashAttribute("successMessage", "Anuncio enviado a todos los usuarios.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar anuncio: " + e.getMessage());
        }
        return "redirect:/notifications/broadcast";
    }

    @PostMapping("/read/{id}")
    @ResponseBody
    public String markRead(@PathVariable Long id) {
        communicationService.markNotificationRead(id);
        return "ok";
    }

    private User getLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username).orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
