package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.NotificationResponse;
import com.revpayproject.revpay.entity.Notification;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import com.revpayproject.revpay.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public List<NotificationResponse> getUserNotifications(User user) {

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getMessage(),
                        n.getType(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .toList();
    }

    public void markAsRead(Long notificationId, User user) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void checkLowBalance(User user, Wallet wallet) {

        if (user.isLowBalanceNotifications()
                && wallet.getBalance().compareTo(new BigDecimal("500")) < 0) {

            createNotification(
                    user,
                    "⚠ Your wallet balance is low: ₹" + wallet.getBalance(),
                    "ALERT"
            );
        }
    }
}