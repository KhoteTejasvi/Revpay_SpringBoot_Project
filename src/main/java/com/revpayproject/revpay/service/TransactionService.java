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
            Pageable pageable,
            Long transactionId,
            String name
    ) {

        Page<Transaction> transactions;

        if (transactionId != null) {

            Page<Transaction> sent =
                    transactionRepository.findByIdAndSender_Email(
                            transactionId, email, pageable);

            Page<Transaction> received =
                    transactionRepository.findByIdAndReceiver_Email(
                            transactionId, email, pageable);

            if (!sent.isEmpty()) {
                transactions = sent;
            } else {
                transactions = received;
            }

        } else if (name != null && !name.trim().isEmpty()) {

            transactions = transactionRepository
                    .searchByNameForUser(name, email, pageable);

        } else {

            transactions = transactionRepository
                    .findBySender_EmailOrReceiver_Email(
                            email, email, pageable
                    );
        }

        return transactions.map(this::mapToResponse);
    }
    private TransactionResponse mapToResponse(Transaction transaction) {

        String senderEmail = transaction.getSender() != null
                ? transaction.getSender().getEmail()
                : null;

        String receiverEmail = transaction.getReceiver() != null
                ? transaction.getReceiver().getEmail()
                : null;

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                senderEmail,
                receiverEmail
        );
    }
}