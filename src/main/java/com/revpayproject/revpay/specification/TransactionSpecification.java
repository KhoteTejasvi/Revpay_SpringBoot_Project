package com.revpayproject.revpay.specification;

import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.enums.TransactionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> hasStatus(TransactionStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Transaction> createdAfter(LocalDateTime startDate) {
        return (root, query, cb) ->
                startDate == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    public static Specification<Transaction> createdBefore(LocalDateTime endDate) {
        return (root, query, cb) ->
                endDate == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }
}