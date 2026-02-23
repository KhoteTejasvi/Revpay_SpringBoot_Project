package com.revpayproject.revpay.dto;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddMoneyRequest {

    @NotNull(message = "Amount required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}