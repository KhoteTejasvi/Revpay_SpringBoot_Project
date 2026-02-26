package com.revpayproject.revpay.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmiScheduleResponse {

    private int emiNumber;
    private LocalDate dueDate;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private BigDecimal totalEmi;
    private BigDecimal remainingBalance;
    private boolean paid;
}