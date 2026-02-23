package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentMethodResponse {

    private Long id;
    private String maskedCardNumber;
    private String expiry;
    private boolean isDefault;
}