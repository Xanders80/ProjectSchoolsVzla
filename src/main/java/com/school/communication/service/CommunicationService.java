package com.school.communication.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.communication.entity.Message;
import com.school.communication.entity.Notification;
import com.school.communication.enums.NotificationType;
import com.school.communication.repository.MessageRepository;
import com.school.communication.repository.NotificationRepository;
import com.school.core.entity.User;
import com.school.core.service.UserService;

@Service
@Transactional
public class CommunicationService {

    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public CommunicationService(MessageRepository messageRepository,
            NotificationRepository notificationRepository,
            UserService userService) {
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    public Message sendMessage(Long senderId, Long receiverId, String subject, String content) {
        User sender = userService.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        User receiver = userService.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setSubject(subject);
        message.setContent(content);

        return messageRepository.save(message);
    }

    public Page<Message> getInbox(Long userId, Pageable pageable) {
        return messageRepository.findByReceiverIdOrderBySentAtDesc(userId, pageable);
    }

    public Page<Message> getSentBox(Long userId, Pageable pageable) {
        return messageRepository.findBySenderIdOrderBySentAtDesc(userId, pageable);
    }

    public Message readMessage(@NonNull Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        // Security check: Only receiver can read and mark as read
        if (!message.getReceiver().getId().equals(userId)) {
            // If sender is reading, just return (or throw if you want to be strict, but
            // sender should be able to view their sent msg)
            if (message.getSender().getId().equals(userId))
                return message;
            throw new SecurityException("Access denied");
        }

        if (!message.isRead()) {
            message.setRead(true);
            messageRepository.save(message);
        }
        return message;
    }

    public long countUnread(Long userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    // Notifications

    public void createNotification(User user, NotificationType type,
            String messageContent) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(messageContent);
        notificationRepository.save(notification);
    }

    public void broadcastNotification(NotificationType type, String messageContent) {
        // Broadcast = Notification with null user (interpreted as Global)
        // OR we create one for every user.
        // For performance in a large system, we'd have a separate "Broadcast" entity.
        // For this MVP, let's just create one with NULL user and update the repo to
        // find those too?
        // OR just iterate all users.
        // Let's use NULL user as logic for "All Users" to save DB space, but then
        // "IsRead" is tricky.
        // Better: For MVP, iterate and save. (Slow but safe for small scope).
        // Actually, let's try the "Broadcast" approach where User is null.
        // But then how do we track if User X read it? We'd need a "BroadcastRead" join
        // table.
        // Let's stick to generating individual notifications for simplicity of "Read"
        // status.
        // Assuming user base is small (< 1000).

        List<User> users = userService.findAllUsers(); // We added this method!
        users.forEach(user -> createNotification(user, type, messageContent));
    }

    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public void markNotificationRead(@NonNull Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}
