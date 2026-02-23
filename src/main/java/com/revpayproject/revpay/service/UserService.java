package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.NotificationPreferenceDto;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String updateNotificationPreferences(String email,
                                                NotificationPreferenceDto dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setTransactionNotifications(dto.isTransactionNotifications());
        user.setEmailNotifications(dto.isEmailNotifications());
        user.setLowBalanceNotifications(dto.isLowBalanceNotifications());

        userRepository.save(user);

        return "Notification preferences updated successfully";
    }
}