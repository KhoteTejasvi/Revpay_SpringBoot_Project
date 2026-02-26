package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.DashboardResponse;
import com.revpayproject.revpay.entity.BusinessProfile;
import com.revpayproject.revpay.repository.BusinessProfileRepository;
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
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final BusinessProfileRepository businessProfileRepository;

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
                .and(TransactionSpecification.startDate(startDate))
                .and(TransactionSpecification.endDate(endDate));

        return transactionRepository.findAll(spec, pageable);
    }

    public List<BusinessProfile> getPendingBusinesses() {
        return businessProfileRepository.findByVerifiedFalse();
    }

    public String approveBusiness(Long id) {

        BusinessProfile profile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        profile.setVerified(true);
        businessProfileRepository.save(profile);

        return "Business approved successfully";
    }

    public String rejectBusiness(Long id) {

        BusinessProfile profile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        profile.setVerified(false);
        businessProfileRepository.save(profile);

        return "Business rejected";
    }
}