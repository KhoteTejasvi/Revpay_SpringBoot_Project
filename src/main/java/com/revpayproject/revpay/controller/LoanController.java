package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.ApplyLoanDto;
import com.revpayproject.revpay.dto.TransactionPinDto;
import com.revpayproject.revpay.entity.Loan;
import com.revpayproject.revpay.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @PostMapping("/apply")
    public String applyLoan(@RequestBody ApplyLoanDto dto) {
        return loanService.applyLoan(getLoggedInEmail(), dto);
    }

    @GetMapping("/my")
    public List<com.revpayproject.revpay.dto.LoanResponse> getMyLoans() {
        return loanService.getMyLoans(getLoggedInEmail());
    }

    @PostMapping("/{id}/repay")
    public String repayLoan(@PathVariable Long id,
                            @RequestBody TransactionPinDto dto) {

        return loanService.repayLoan(
                id,
                getLoggedInEmail(),
                dto.getPin()
        );
    }
}