package com.uniskills.main.controller;

import com.uniskills.main.model.Notification;
import com.uniskills.main.service.NotificationService;
import com.uniskills.main.repository.UserRepository;
import com.uniskills.main.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Notification> getNotifications(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return notificationService.getUserNotifications(user.getId());
    }

    @PutMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return notificationService.getUnreadCount(user.getId());
    }
}