package com.revpayproject.revpay.service;

import com.revpayproject.revpay.entity.Notification;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(User user, String message, String type) {

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public void markAsRead(Notification notification) {
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}