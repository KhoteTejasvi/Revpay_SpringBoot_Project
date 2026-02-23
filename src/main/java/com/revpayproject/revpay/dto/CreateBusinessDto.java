package com.revpayproject.revpay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBusinessDto {

    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
    private String contactNumber;
}