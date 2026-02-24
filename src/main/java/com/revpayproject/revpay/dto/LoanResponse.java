package com.revpayproject.revpay.dto;

import com.revpayproject.revpay.enums.LoanStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class LoanResponse {

    private Long id;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private BigDecimal remainingAmount;
    private LoanStatus status;
    private LocalDateTime appliedAt;
}