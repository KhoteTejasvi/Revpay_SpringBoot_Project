package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.ApplyLoanDto;
import com.revpayproject.revpay.entity.Loan;
import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.enums.LoanStatus;
import com.revpayproject.revpay.enums.Role;
import com.revpayproject.revpay.repository.LoanRepository;
import com.revpayproject.revpay.repository.TransactionRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.revpayproject.revpay.dto.LoanResponse;
import com.revpayproject.revpay.enums.TransactionStatus;
import jakarta.transaction.Transactional;
import com.revpayproject.revpay.entity.Wallet;
import java.math.RoundingMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    public String applyLoan(String email, ApplyLoanDto dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.BUSINESS) {
            throw new RuntimeException("Only BUSINESS users can apply for loan");
        }

        BigDecimal interestRate = BigDecimal.valueOf(10);

        BigDecimal monthlyRate = interestRate
                .divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal emi = calculateEMI(
                dto.getLoanAmount(),
                monthlyRate,
                dto.getTenureMonths()
        );

        Loan loan = new Loan();
        loan.setLoanAmount(dto.getLoanAmount());
        loan.setInterestRate(interestRate);
        loan.setTenureMonths(dto.getTenureMonths());
        loan.setEmiAmount(emi);
        loan.setRemainingAmount(emi.multiply(BigDecimal.valueOf(dto.getTenureMonths())));
        loan.setStatus(LoanStatus.PENDING);
        loan.setAppliedAt(LocalDateTime.now());
        loan.setBusinessUser(user);

        loanRepository.save(loan);

        return "Loan application submitted successfully";
    }

    private BigDecimal calculateEMI(BigDecimal principal,
                                    BigDecimal monthlyRate,
                                    int months) {

        double P = principal.doubleValue();
        double R = monthlyRate.doubleValue();
        int N = months;

        double emi = (P * R * Math.pow(1 + R, N)) /
                (Math.pow(1 + R, N) - 1);

        return BigDecimal.valueOf(emi);
    }

    public List<LoanResponse> getMyLoans(String email) {

        return loanRepository.findByBusinessUser_Email(email)
                .stream()
                .map(loan -> LoanResponse.builder()
                        .id(loan.getId())
                        .loanAmount(loan.getLoanAmount())
                        .interestRate(loan.getInterestRate())
                        .tenureMonths(loan.getTenureMonths())
                        .emiAmount(loan.getEmiAmount())
                        .remainingAmount(loan.getRemainingAmount())
                        .status(loan.getStatus())
                        .appliedAt(loan.getAppliedAt())
                        .build())
                .toList();
    }

    @Transactional
    public String approveLoan(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Only pending loans can be approved");
        }

        User businessUser = loan.getBusinessUser();

        Wallet wallet = walletRepository.findByUser(businessUser)
                .orElseThrow(() -> new RuntimeException("Business wallet not found"));

        // Credit wallet
        wallet.setBalance(
                wallet.getBalance().add(loan.getLoanAmount())
        );

        loan.setStatus(LoanStatus.ACTIVE);
        loan.setApprovedAt(LocalDateTime.now());

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(loan.getLoanAmount());
        transaction.setType("LOAN_CREDIT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(null);
        transaction.setReceiver(businessUser);

        transactionRepository.save(transaction);

        notificationService.createNotification(
                businessUser,
                "Loan approved and credited ₹" + loan.getLoanAmount(),
                "LOAN"
        );

        return "Loan approved successfully";
    }

    @Transactional
    public String repayLoan(Long loanId, String email) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getBusinessUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access");
        }

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Loan is not active");
        }

        Wallet wallet = walletRepository.findByUser(loan.getBusinessUser())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal emi = loan.getEmiAmount();

        if (wallet.getBalance().compareTo(emi) < 0) {
            throw new RuntimeException("Insufficient balance to pay EMI");
        }

        // Deduct EMI from wallet
        wallet.setBalance(wallet.getBalance().subtract(emi));

        // Reduce remaining amount
        loan.setRemainingAmount(
                loan.getRemainingAmount().subtract(emi)
        );

        // If fully paid
        if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
            loan.setRemainingAmount(BigDecimal.ZERO);
        }

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(emi);
        transaction.setType("LOAN_REPAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(loan.getBusinessUser());
        transaction.setReceiver(null);

        transactionRepository.save(transaction);

        notificationService.createNotification(
                loan.getBusinessUser(),
                "EMI paid ₹" + emi,
                "LOAN"
        );

        return "EMI paid successfully";
    }
}