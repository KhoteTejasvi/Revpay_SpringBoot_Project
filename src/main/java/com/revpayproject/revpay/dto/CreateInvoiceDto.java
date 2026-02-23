package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateInvoiceDto {

    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private LocalDateTime dueDate;
    private List<InvoiceItemDto> items;
}