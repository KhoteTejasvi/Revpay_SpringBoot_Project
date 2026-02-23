package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SendRequestDto {
    private String receiverEmail;
    private BigDecimal amount;
    private String note;
}
