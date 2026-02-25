package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TopCustomerResponse {

    private String customerEmail;
    private BigDecimal totalPaid;
}