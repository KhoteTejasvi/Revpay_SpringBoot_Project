package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.NotificationResponse;
import com.revpayproject.revpay.entity.Notification;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    private User getUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow();
    }

    @GetMapping
    public List<NotificationResponse> getNotifications() {
        return notificationService.getUserNotifications(getUser());
    }

    @PutMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id) {

        notificationService.markAsRead(id, getUser());

        return "Notification marked as read";
    }
}