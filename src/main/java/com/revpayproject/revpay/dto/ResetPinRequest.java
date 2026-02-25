package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPinRequest {

    private String email;
    private String question;
    private String answer;
    private String newPin;
}