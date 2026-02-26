package com.revpayproject.revpay.entity;

import com.revpayproject.revpay.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal loanAmount;
    private BigDecimal interestRate; // annual %
    private Integer tenureMonths;

    private BigDecimal emiAmount;
    private BigDecimal remainingAmount;

    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @ManyToOne
    @JoinColumn(name = "business_user_id")
    private User businessUser;

    private String rejectionReason;

    private String documentName;

    private String documentPath;
}