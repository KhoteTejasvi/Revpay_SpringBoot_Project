package com.revpayproject.revpay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int emiNumber;

    private LocalDate dueDate;

    private BigDecimal principalComponent;

    private BigDecimal interestComponent;

    private BigDecimal totalEmi;

    private BigDecimal remainingBalance;

    private boolean paid;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;
}