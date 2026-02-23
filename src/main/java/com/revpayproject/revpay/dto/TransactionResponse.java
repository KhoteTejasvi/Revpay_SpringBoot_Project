package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.revpayproject.revpay.enums.TransactionStatus;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String type;
    private TransactionStatus status;
    private LocalDateTime createdAt;

    private String senderEmail;
    private String receiverEmail;
}