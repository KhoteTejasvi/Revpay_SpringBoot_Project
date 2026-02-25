package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.DashboardSummaryResponse;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import com.revpayproject.revpay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserService userService;

    public DashboardSummaryResponse getDashboardSummary() {

        User user = userService.getLoggedInUser();

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal totalSent = transactionRepository.getTotalSent(user.getId());
        BigDecimal totalReceived = transactionRepository.getTotalReceived(user.getId());

        long totalTransactions =
                transactionRepository.countBySender_IdOrReceiver_Id(user.getId(), user.getId());

        long activeLoans =
                loanRepository.countByBusinessUser_EmailAndStatus(user.getEmail(),
                        com.revpayproject.revpay.enums.LoanStatus.ACTIVE);

        long pendingInvoices =
                invoiceRepository.countByBusinessUser_EmailAndStatus(user.getEmail(),
                        com.revpayproject.revpay.enums.InvoiceStatus.SENT);

        return new DashboardSummaryResponse(
                wallet.getBalance(),
                totalSent,
                totalReceived,
                totalTransactions,
                activeLoans,
                pendingInvoices
        );
    }
}