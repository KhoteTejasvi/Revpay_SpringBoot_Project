package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.MoneyRequest;
import com.revpayproject.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {

    List<MoneyRequest> findByReceiver(User receiver);

    List<MoneyRequest> findBySender(User sender);
}
