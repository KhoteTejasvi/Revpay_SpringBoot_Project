package com.revpayproject.revpay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maskedCardNumber;
    private String last4Digits;

    private String expiry;
    private String encryptedCvv;
    private String billingAddress;

    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}