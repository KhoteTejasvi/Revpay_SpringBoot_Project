package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        return "User Registered Successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody User loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user.getEmail());
    }
}