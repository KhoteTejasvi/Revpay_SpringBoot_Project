package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.NotificationPreferenceResponse;
import com.revpayproject.revpay.dto.NotificationResponse;
import com.revpayproject.revpay.entity.Notification;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import com.revpayproject.revpay.repository.NotificationPreferenceRepository;
import com.revpayproject.revpay.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.revpayproject.revpay.dto.NotificationPreferenceDto;
import com.revpayproject.revpay.entity.NotificationPreference;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public void createNotification(User user, String message, String type) {

        NotificationPreference pref =
                preferenceRepository.findByUser(user).orElse(null);

        if (pref != null) {

            if (type.equals("TRANSACTION") && !pref.isTransactionNotifications())
                return;

            if (type.equals("ALERT") && !pref.isLowBalanceNotifications())
                return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }


    public Page<NotificationResponse> getUserNotifications(User user, int page, int size) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, size))
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getMessage(),
                        n.getType(),
                        n.isRead(),
                        n.getCreatedAt()
                ));
    }

    public void markAsRead(Long notificationId, User user) {

        Notification notification =
                notificationRepository.findByIdAndUser(notificationId, user)
                        .orElseThrow(() -> new RuntimeException("Notification not found"));

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

    public NotificationPreferenceResponse updatePreferences(
            User user,
            NotificationPreferenceDto dto) {

        NotificationPreference pref =
                preferenceRepository.findByUser(user)
                        .orElse(new NotificationPreference());

        pref.setUser(user);
        pref.setTransactionNotifications(dto.isTransactionNotifications());
        pref.setEmailNotifications(dto.isEmailNotifications());
        pref.setLowBalanceNotifications(dto.isLowBalanceNotifications());

        NotificationPreference saved = preferenceRepository.save(pref);

        return new NotificationPreferenceResponse(
                saved.isTransactionNotifications(),
                saved.isEmailNotifications(),
                saved.isLowBalanceNotifications()
        );
    }
}