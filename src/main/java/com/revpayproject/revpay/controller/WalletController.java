package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.*;
import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.enums.TransactionStatus;
import com.revpayproject.revpay.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @GetMapping("/balance")
    public BigDecimal getBalance() {
        return walletService.getBalance(getLoggedInEmail());
    }

    @PostMapping("/add")
    public String addMoney(@Valid @RequestBody AddMoneyRequest request) {
        return walletService.addMoney(getLoggedInEmail(), request);
    }

    @PostMapping("/transfer")
    public String transferMoney(@Valid @RequestBody TransferRequest request) {
        return walletService.transferMoney(getLoggedInEmail(), request);
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> getTransactions() {
        return walletService.getTransactions(getLoggedInEmail());
    }

    @PostMapping("/add-from-card")
    public String addMoneyFromCard(@RequestBody AddMoneyFromCardDto dto) {
        return walletService.addMoneyFromCard(
                getLoggedInEmail(),
                dto.getAmount()
        );
    }

    @PostMapping("/withdraw")
    public String withdrawMoney(@RequestBody WithdrawRequestDto dto) {
        return walletService.withdrawMoney(
                getLoggedInEmail(),
                dto.getAmount()
        );
    }

    @GetMapping("/transactions/filter")
    public Page<Transaction> filterTransactions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,
            Pageable pageable) {

        return walletService.filterTransactions(
                getLoggedInEmail(),
                type,
                status,
                minAmount,
                maxAmount,
                startDate,
                endDate,
                pageable
        );
    }

}