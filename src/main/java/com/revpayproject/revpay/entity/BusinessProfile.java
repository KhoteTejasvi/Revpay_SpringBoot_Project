package com.revpayproject.revpay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BusinessProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
    private String contactNumber;

    private boolean verified = false;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}