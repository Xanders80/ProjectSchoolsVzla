package com.school.web.controller.communication;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.communication.enums.NotificationType;
import com.school.communication.service.CommunicationService;
import com.school.core.entity.User;
import com.school.core.service.UserService;

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
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) String message,
            RedirectAttributes redirectAttributes) {

        if (type == null || message == null || message.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Todos los campos obligatorios deben ser completados.");
            return "redirect:/notifications/broadcast";
        }

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
    public String markRead(@PathVariable @NonNull Long id) {
        communicationService.markNotificationRead(id);
        return "ok";
    }

    private User getLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username).orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
