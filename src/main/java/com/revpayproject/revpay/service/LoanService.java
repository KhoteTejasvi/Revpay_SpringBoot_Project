package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.ApplyLoanDto;
import com.revpayproject.revpay.entity.Loan;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.enums.LoanStatus;
import com.revpayproject.revpay.enums.Role;
import com.revpayproject.revpay.repository.LoanRepository;
import com.revpayproject.revpay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public String applyLoan(String email, ApplyLoanDto dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.BUSINESS) {
            throw new RuntimeException("Only BUSINESS users can apply for loan");
        }

        BigDecimal interestRate = BigDecimal.valueOf(10); // 10% fixed for now

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(12), 4, BigDecimal.ROUND_HALF_UP)
                .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);

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

    public List<Loan> getMyLoans(String email) {
        return loanRepository.findByBusinessUser_Email(email);
    }
}