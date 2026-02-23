package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.revpayproject.revpay.enums.TransactionStatus;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderOrReceiver(User sender, User receiver);
    long countByStatus(TransactionStatus status);
}