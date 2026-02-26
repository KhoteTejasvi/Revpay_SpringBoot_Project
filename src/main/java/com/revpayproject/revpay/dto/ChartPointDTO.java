package com.revpayproject.revpay.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartPointDTO {

    private String label;
    private Double value;
}