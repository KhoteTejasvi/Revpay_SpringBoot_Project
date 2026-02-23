package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private long totalUsers;
    private BigDecimal totalWalletBalance;
    private long totalTransactions;
    private long successfulTransactions;
    private long failedTransactions;
}