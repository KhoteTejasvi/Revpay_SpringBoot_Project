package com.revpayproject.revpay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean transactionNotifications = true;
    private boolean emailNotifications = true;
    private boolean lowBalanceNotifications = true;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}