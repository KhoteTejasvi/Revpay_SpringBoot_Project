package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.NotificationPreferenceDto;
import com.revpayproject.revpay.dto.NotificationPreferenceResponse;
import com.revpayproject.revpay.dto.NotificationResponse;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public Page<NotificationResponse> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return notificationService.getUserNotifications(getUser(), page, size);
    }

    @PutMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id) {

        notificationService.markAsRead(id, getUser());

        return "Notification marked as read";
    }

    @PutMapping("/preferences")
    public NotificationPreferenceResponse updatePreferences(
            @RequestBody NotificationPreferenceDto dto) {

        return notificationService.updatePreferences(getUser(), dto);
    }
}