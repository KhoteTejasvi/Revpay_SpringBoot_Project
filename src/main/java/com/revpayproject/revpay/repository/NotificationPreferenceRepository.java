package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.NotificationPreference;   // ✅ correct
import com.revpayproject.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, Long> {

    Optional<NotificationPreference> findByUser(User user);
}