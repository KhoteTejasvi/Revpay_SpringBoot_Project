package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.TransactionResponse;
import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<TransactionResponse> getUserTransactionsPaginated(
            String email,
            Pageable pageable
    ) {
        return transactionRepository
                .findBySender_EmailOrReceiver_Email(email, email, pageable)
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getType(),
                        transaction.getStatus(),
                        transaction.getCreatedAt(),
                        transaction.getSender() != null
                                ? transaction.getSender().getEmail()
                                : null,
                        transaction.getReceiver() != null
                                ? transaction.getReceiver().getEmail()
                                : null
                ));
    }
}