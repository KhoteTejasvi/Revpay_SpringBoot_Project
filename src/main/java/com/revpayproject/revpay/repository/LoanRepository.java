package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.Loan;
import com.revpayproject.revpay.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByBusinessUser_Email(String email);

    Page<Loan> findByBusinessUser_Email(String email, Pageable pageable);

    List<Loan> findByStatus(LoanStatus status);

    long countByBusinessUser_EmailAndStatus(String email, LoanStatus status);
}