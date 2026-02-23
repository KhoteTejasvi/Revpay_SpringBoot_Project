package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawRequestDto {

    private BigDecimal amount;
    private String bankAccountNumber;  // simulated
}