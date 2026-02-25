package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardSummaryResponse {

    private BigDecimal walletBalance;
    private BigDecimal totalSent;
    private BigDecimal totalReceived;
    private long totalTransactions;
    private long activeLoans;
    private long pendingInvoices;
}