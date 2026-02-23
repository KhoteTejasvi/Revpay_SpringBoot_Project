package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceItemDto {

    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal tax;
}