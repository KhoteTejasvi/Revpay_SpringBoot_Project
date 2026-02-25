package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.SecurityQuestion;
import com.revpayproject.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityQuestionRepository
        extends JpaRepository<SecurityQuestion, Long> {

    List<SecurityQuestion> findByUser(User user);
}