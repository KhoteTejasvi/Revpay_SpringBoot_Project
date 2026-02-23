package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.*;
import com.revpayproject.revpay.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
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
}