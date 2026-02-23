package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.DashboardResponse;
import com.revpayproject.revpay.repository.TransactionRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public DashboardResponse getDashboardStats() {

        long totalUsers = userRepository.count();
        long totalTransactions = transactionRepository.count();
        long successful = transactionRepository.countByStatus("SUCCESS");
        long failed = transactionRepository.countByStatus("FAILED");

        return new DashboardResponse(
                totalUsers,
                walletRepository.getTotalWalletBalance(),
                totalTransactions,
                successful,
                failed
        );
    }
}