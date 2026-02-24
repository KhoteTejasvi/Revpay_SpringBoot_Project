package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ApplyLoanDto {

    private BigDecimal loanAmount;
    private Integer tenureMonths;
}