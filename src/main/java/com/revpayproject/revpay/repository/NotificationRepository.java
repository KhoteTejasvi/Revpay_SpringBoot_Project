package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.Notification;
import com.revpayproject.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}