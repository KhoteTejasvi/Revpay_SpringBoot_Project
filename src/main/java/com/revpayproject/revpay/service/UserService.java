package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.LoginRequest;
import com.revpayproject.revpay.dto.RegisterRequest;
import com.revpayproject.revpay.entity.User;

public interface UserService {

    User register(RegisterRequest request);

    String login(LoginRequest request);
}