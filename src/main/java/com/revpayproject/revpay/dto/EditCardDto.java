package com.revpayproject.revpay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditCardDto {

    @NotBlank(message = "Expiry date required")
    private String expiry;

    @NotBlank(message = "Billing address required")
    private String billingAddress;
}