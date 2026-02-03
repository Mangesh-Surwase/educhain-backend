package com.uniskills.main.service;

import com.uniskills.main.model.Notification;
import com.uniskills.main.model.User;
import com.uniskills.main.repository.NotificationRepository;
import com.uniskills.main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    //  SEND NOTIFICATION (Internal Use)
    public void sendNotification(User recipient, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    //  GET MY NOTIFICATIONS
    public List<Notification> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    // MARK AS READ
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow();
        n.setRead(true);
        notificationRepository.save(n);
    }

    //  UNREAD COUNT
    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }
}