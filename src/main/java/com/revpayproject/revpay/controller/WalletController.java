package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.AddMoneyRequest;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @GetMapping("/balance")
    public BigDecimal getBalance() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        Wallet wallet = walletRepository.findByUser(user).orElseThrow();

        return wallet.getBalance();
    }

    @PostMapping("/add")
    public String addMoney(@RequestBody AddMoneyRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow();

        Wallet wallet = walletRepository.findByUser(user).orElseThrow();

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);

        return "Money Added Successfully";
    }
}