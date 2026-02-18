package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.LoginRequest;
import com.revpayproject.revpay.dto.RegisterRequest;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
