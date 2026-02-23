package com.revpayproject.revpay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal tax;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}