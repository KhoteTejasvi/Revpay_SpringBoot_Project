package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.ApplyLoanDto;
import com.revpayproject.revpay.entity.*;
import com.revpayproject.revpay.enums.LoanStatus;
import com.revpayproject.revpay.enums.Role;
import com.revpayproject.revpay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.revpayproject.revpay.dto.LoanResponse;
import com.revpayproject.revpay.enums.TransactionStatus;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import com.revpayproject.revpay.dto.EmiScheduleResponse;
import java.math.RoundingMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final EmiScheduleRepository emiScheduleRepository;


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
        loan.setRemainingAmount(
                emi.multiply(BigDecimal.valueOf(dto.getTenureMonths()))
                        .setScale(2, RoundingMode.HALF_UP)
        );
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

        return BigDecimal.valueOf(emi)
                .setScale(2, RoundingMode.HALF_UP);
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
        generateEmiSchedule(loan);

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
    public String repayLoan(Long loanId,
                            String email,
                            String pin) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getBusinessUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access");
        }

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Loan is not active");
        }

        User businessUser = loan.getBusinessUser();

        // 🔐 Enforce Transaction PIN
        userService.validatePin(businessUser, pin);

        Wallet wallet = walletRepository.findByUser(businessUser)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal emi = loan.getEmiAmount();

        if (wallet.getBalance().compareTo(emi) < 0) {
            throw new RuntimeException("Insufficient balance to pay EMI");
        }

        wallet.setBalance(wallet.getBalance().subtract(emi));

        loan.setRemainingAmount(
                loan.getRemainingAmount().subtract(emi)
        );

        if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
            loan.setRemainingAmount(BigDecimal.ZERO);
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(emi);
        transaction.setType("LOAN_REPAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(businessUser);
        transaction.setReceiver(null);

        transactionRepository.save(transaction);

        notificationService.createNotification(
                businessUser,
                "EMI paid ₹" + emi,
                "LOAN"
        );

        return "EMI paid successfully";
    }

    public Page<LoanResponse> getMyLoansPaginated(
            String email,
            Pageable pageable
    ) {
        return loanRepository
                .findByBusinessUser_Email(email, pageable)
                .map(loan -> LoanResponse.builder()
                        .id(loan.getId())
                        .loanAmount(loan.getLoanAmount())
                        .interestRate(loan.getInterestRate())
                        .tenureMonths(loan.getTenureMonths())
                        .emiAmount(loan.getEmiAmount())
                        .remainingAmount(loan.getRemainingAmount())
                        .status(loan.getStatus())
                        .appliedAt(loan.getAppliedAt())
                        .build());
    }

    public void rejectLoan(Long loanId, String reason) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Only pending loans can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionReason(reason);

        loanRepository.save(loan);
    }

    public void uploadDocument(Long loanId, MultipartFile file) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setDocumentName(file.getOriginalFilename());
        loan.setDocumentPath("simulated/path/" + file.getOriginalFilename());

        loanRepository.save(loan);
    }
    public Page<EmiScheduleResponse> getSchedule(Long loanId, Pageable pageable) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getBusinessUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access");
        }

        return emiScheduleRepository
                .findByLoan_IdOrderByEmiNumberAsc(loanId, pageable)
                .map(emi -> EmiScheduleResponse.builder()
                        .emiNumber(emi.getEmiNumber())
                        .dueDate(emi.getDueDate())
                        .principalComponent(emi.getPrincipalComponent())
                        .interestComponent(emi.getInterestComponent())
                        .totalEmi(emi.getTotalEmi())
                        .remainingBalance(emi.getRemainingBalance())
                        .paid(emi.isPaid())
                        .build());
    }

    private void generateEmiSchedule(Loan loan) {

        BigDecimal principal = loan.getLoanAmount();
        BigDecimal annualRate = loan.getInterestRate();

        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        BigDecimal remainingPrincipal = principal;
        int tenure = loan.getTenureMonths();

        for (int i = 1; i <= tenure; i++) {

            BigDecimal interestComponent = remainingPrincipal
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalComponent = loan.getEmiAmount()
                    .subtract(interestComponent)
                    .setScale(2, RoundingMode.HALF_UP);

            remainingPrincipal = remainingPrincipal
                    .subtract(principalComponent)
                    .setScale(2, RoundingMode.HALF_UP);

            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO;
            }

            EmiSchedule emi = new EmiSchedule();
            emi.setLoan(loan);
            emi.setEmiNumber(i);
            emi.setPrincipalComponent(principalComponent);
            emi.setInterestComponent(interestComponent);
            emi.setTotalEmi(loan.getEmiAmount());
            emi.setRemainingBalance(remainingPrincipal);
            emi.setDueDate(LocalDateTime.now().plusMonths(i).toLocalDate());
            emi.setPaid(false);

            emiScheduleRepository.save(emi);
        }
    }
}