package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {
    long countByStatus(TransactionStatus status);
    List<Transaction> findBySenderOrReceiver(User sender, User receiver);
    List<Transaction> findBySender_EmailOrReceiver_Email(String senderEmail,String receiverEmail);
}