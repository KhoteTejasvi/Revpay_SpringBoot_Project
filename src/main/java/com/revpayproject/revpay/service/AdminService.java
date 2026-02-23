package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.DashboardResponse;
import com.revpayproject.revpay.repository.TransactionRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.revpayproject.revpay.enums.TransactionStatus;
import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.specification.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public DashboardResponse getDashboardStats() {

        long totalUsers = userRepository.count();
        long totalTransactions = transactionRepository.count();
        long successful = transactionRepository.countByStatus(TransactionStatus.SUCCESS);
        long failed = transactionRepository.countByStatus(TransactionStatus.FAILED);

        return new DashboardResponse(
                totalUsers,
                walletRepository.getTotalWalletBalance(),
                totalTransactions,
                successful,
                failed
        );
    }

    public Page<Transaction> filterTransactions(
            TransactionStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        Specification<Transaction> spec = Specification
                .where(TransactionSpecification.hasStatus(status))
                .and(TransactionSpecification.createdAfter(startDate))
                .and(TransactionSpecification.createdBefore(endDate));

        return transactionRepository.findAll(spec, pageable);
    }
}