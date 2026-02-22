package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.AddMoneyRequest;
import com.revpayproject.revpay.dto.TransactionResponse;
import com.revpayproject.revpay.dto.TransferRequest;
import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import com.revpayproject.revpay.repository.TransactionRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/balance")
    public BigDecimal getBalance() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return wallet.getBalance();
    }

    @PostMapping("/add")
    public String addMoney(@RequestBody AddMoneyRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);

        return "Money Added Successfully";
    }

    @PostMapping("/transfer")
    public String transferMoney(@RequestBody TransferRequest request) {

        String senderEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            return "Insufficient Balance";
        }

        // Deduct from sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));

        // Add to receiver
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType("TRANSFER");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        transactionRepository.save(transaction);

        return "Transfer Successful";
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> getTransactions() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions =
                transactionRepository.findBySenderOrReceiver(user, user);

        return transactions.stream()
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getCreatedAt(),
                        tx.getSender().getEmail(),
                        tx.getReceiver().getEmail()
                ))
                .toList();
    }
}