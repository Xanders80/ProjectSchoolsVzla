package com.school.communication.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.communication.entity.Notification;
import com.school.communication.enums.NotificationType;
import com.school.communication.repository.NotificationRepository;
import com.school.core.entity.User;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(String title, String message, NotificationType type, User user) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setUser(user);
        notification.setRead(false);
        notification.setCreatedAt(java.time.LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
}