package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {
    long countByStatus(TransactionStatus status);
    List<Transaction> findBySenderOrReceiver(User sender, User receiver);
    List<Transaction> findBySender_EmailOrReceiver_Email(String senderEmail,String receiverEmail);

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.sender.id = :userId")
    BigDecimal getTotalSent(Long userId);

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.receiver.id = :userId")
    BigDecimal getTotalReceived(Long userId);

    long countBySender_IdOrReceiver_Id(Long senderId, Long receiverId);

    @Query("""
SELECT COALESCE(SUM(t.amount),0)
FROM Transaction t
WHERE t.receiver.id = :userId
AND t.createdAt BETWEEN :start AND :end
""")
    BigDecimal getRevenueBetweenDates(
            Long userId,
            LocalDateTime start,
            LocalDateTime end);
}