package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationPreferenceResponse {

    private boolean transactionNotifications;
    private boolean emailNotifications;
    private boolean lowBalanceNotifications;
}