package com.revpayproject.revpay.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceResponse {

    private Long id;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime dueDate;
}