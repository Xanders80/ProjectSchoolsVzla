package com.school.web.controller.advice;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.school.communication.service.CommunicationService;
import com.school.core.entity.User;
import com.school.core.service.UserService;

@ControllerAdvice
public class GlobalDataControllerAdvice {

    private final CommunicationService communicationService;
    private final UserService userService;

    public GlobalDataControllerAdvice(CommunicationService communicationService, UserService userService) {
        this.communicationService = communicationService;
        this.userService = userService;
    }

    @ModelAttribute("unreadNotificationsCount")
    public long unreadNotificationsCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            return userService.findByUsername(username)
                    .map(User::getId)
                    .map(communicationService::countUnreadNotifications)
                    .orElse(0L);
        }
        return 0L;
    }
}
