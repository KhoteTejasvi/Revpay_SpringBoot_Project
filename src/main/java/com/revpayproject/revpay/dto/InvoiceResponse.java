package com.revpayproject.revpay.dto;

import com.revpayproject.revpay.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InvoiceResponse {

    private Long id;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private LocalDateTime dueDate;
    private InvoiceStatus status;
}