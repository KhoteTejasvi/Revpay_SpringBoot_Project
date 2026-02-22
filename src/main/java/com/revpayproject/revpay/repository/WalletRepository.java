package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);
}