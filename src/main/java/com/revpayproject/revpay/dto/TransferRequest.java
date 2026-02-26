package com.revpayproject.revpay.dto;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotBlank(message = "Receiver identifier required")
    private String identifier;   // email / phone / id / username

    @NotNull(message = "Amount required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Transaction PIN required")
    @Size(min = 4, max = 4, message = "PIN must be 4 digits")
    private String transactionPin;
}