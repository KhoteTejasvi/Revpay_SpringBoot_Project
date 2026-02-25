package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnlockAccountRequest {

    private String email;
    private String question;
    private String answer;
}