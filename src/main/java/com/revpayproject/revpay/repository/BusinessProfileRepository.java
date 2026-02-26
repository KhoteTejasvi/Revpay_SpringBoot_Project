package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.BusinessProfile;
import com.revpayproject.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessProfileRepository
        extends JpaRepository<BusinessProfile, Long> {

    List<BusinessProfile> findByVerifiedFalse();

    Optional<BusinessProfile> findByUser(User user);
}