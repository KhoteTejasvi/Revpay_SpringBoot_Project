package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PaymentTrendResponse {

    private LocalDate date;
    private BigDecimal amount;
}