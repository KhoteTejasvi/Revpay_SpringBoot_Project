package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String type;
    private LocalDateTime createdAt;

    private String senderEmail;
    private String receiverEmail;
}