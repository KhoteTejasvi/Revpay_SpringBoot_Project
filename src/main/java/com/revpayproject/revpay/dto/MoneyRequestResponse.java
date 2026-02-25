package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MoneyRequestResponse {

    private Long id;
    private BigDecimal amount;
    private String note;
    private String status;
    private LocalDateTime createdAt;

    private String senderEmail;
    private String receiverEmail;
}