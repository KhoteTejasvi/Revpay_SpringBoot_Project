package com.revpayproject.revpay.service;

import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getUserTransactions(String email) {
        return transactionRepository
                .findBySender_EmailOrReceiver_Email(email, email);
    }
}