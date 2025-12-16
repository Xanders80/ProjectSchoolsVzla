package com.school.web.controller.communication;

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
@RequestMapping("/messages")
public class CommunicationController {

    private final CommunicationService communicationService;
    private final UserService userService;

    public CommunicationController(CommunicationService communicationService, UserService userService) {
        this.communicationService = communicationService;
        this.userService = userService;
    }

    @GetMapping
    public String inbox(Model model, @PageableDefault(size = 10) Pageable pageable) {
        User user = getLoggedUser();
        model.addAttribute("messages", communicationService.getInbox(user.getId(), pageable));
        model.addAttribute("title", "Bandeja de Entrada");
        model.addAttribute("isInbox", true);
        return "communication/inbox";
    }

    @GetMapping("/sent")
    public String sent(Model model, @PageableDefault(size = 10) Pageable pageable) {
        User user = getLoggedUser();
        model.addAttribute("messages", communicationService.getSentBox(user.getId(), pageable));
        model.addAttribute("title", "Enviados");
        model.addAttribute("isInbox", false);
        return "communication/inbox"; // Reuse list view
    }

    @GetMapping("/compose")
    public String composeForm(Model model) {
        // In a real app, you might use an autocomplete API for users.
        // For MVP, passing all users (might be heavy, limit to Roles in future)
        model.addAttribute("users", userService.findAllUsers());
        return "communication/compose";
    }

    @PostMapping("/send")
    public String sendMessage(
            @RequestParam Long receiverId,
            @RequestParam String subject,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {
        try {
            User sender = getLoggedUser();
            communicationService.sendMessage(sender.getId(), receiverId, subject, content);
            redirectAttributes.addFlashAttribute("successMessage", "Mensaje enviado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar mensaje: " + e.getMessage());
            return "redirect:/messages/compose";
        }
        return "redirect:/messages/sent";
    }

    @GetMapping("/read/{id}")
    public String readMessage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = getLoggedUser();
            model.addAttribute("message", communicationService.readMessage(id, user.getId()));
            return "communication/read";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo leer el mensaje: " + e.getMessage());
            return "redirect:/messages";
        }
    }

    private User getLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username).orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
