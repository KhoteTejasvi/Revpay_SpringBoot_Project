package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCardDto {

    private String cardNumber;
    private String expiry;
    private String cvv;
    private String billingAddress;
}