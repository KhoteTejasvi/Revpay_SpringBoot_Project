package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationPreferenceDto {

    private boolean transactionNotifications;
    private boolean emailNotifications;
    private boolean lowBalanceNotifications;
}