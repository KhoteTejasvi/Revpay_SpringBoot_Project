package com.revpayproject.revpay.specification;

import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.enums.TransactionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> hasType(String type) {
        return (root, query, cb) ->
                type == null ? null :
                        cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> hasStatus(TransactionStatus status) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("status"), status);
    }

    public static Specification<Transaction> minAmount(BigDecimal min) {
        return (root, query, cb) ->
                min == null ? null :
                        cb.greaterThanOrEqualTo(root.get("amount"), min);
    }

    public static Specification<Transaction> maxAmount(BigDecimal max) {
        return (root, query, cb) ->
                max == null ? null :
                        cb.lessThanOrEqualTo(root.get("amount"), max);
    }

    public static Specification<Transaction> startDate(LocalDateTime start) {
        return (root, query, cb) ->
                start == null ? null :
                        cb.greaterThanOrEqualTo(root.get("createdAt"), start);
    }

    public static Specification<Transaction> endDate(LocalDateTime end) {
        return (root, query, cb) ->
                end == null ? null :
                        cb.lessThanOrEqualTo(root.get("createdAt"), end);
    }
}