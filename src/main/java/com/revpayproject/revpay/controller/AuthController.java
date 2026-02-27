package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.ForgotPasswordRequest;
import com.revpayproject.revpay.dto.LoginRequest;
import com.revpayproject.revpay.dto.RegisterRequest;
import com.revpayproject.revpay.dto.UnlockAccountRequest;
import com.revpayproject.revpay.entity.SecurityQuestion;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import com.revpayproject.revpay.repository.SecurityQuestionRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import com.revpayproject.revpay.service.AccountLockService;
import com.revpayproject.revpay.security.JwtService;
import com.revpayproject.revpay.service.SecurityQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.revpayproject.revpay.enums.Role;
import jakarta.validation.Valid;
import com.revpayproject.revpay.dto.AuthResponse;
import com.revpayproject.revpay.dto.ResetPinRequest;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final SecurityQuestionRepository securityQuestionRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AccountLockService accountLockService;
    private final SecurityQuestionService securityQuestionService;
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", "Phone number already exists"));
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

// ✅ ROLE FIX
        if (request.getRole() != null &&
                request.getRole().equalsIgnoreCase("BUSINESS")) {

            user.setRole(Role.BUSINESS);

        } else {

            user.setRole(Role.USER);
        }

        // ✅ IMPORTANT — SAVE USER FIRST
        User savedUser = userRepository.save(user);

        // 🔥 SAVE SECURITY QUESTIONS
        if (request.getSecurityQuestions() != null) {

            List<SecurityQuestion> questions =
                    request.getSecurityQuestions().stream()
                            .map(q -> {
                                SecurityQuestion sq = new SecurityQuestion();
                                sq.setQuestion(q.getQuestion());
                                sq.setAnswer(passwordEncoder.encode(q.getAnswer()));
                                sq.setUser(savedUser); // ✅ FIXED
                                return sq;
                            }).toList();

            securityQuestionRepository.saveAll(questions);
        }

        // ✅ CREATE WALLET
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);   // ✅ FIXED
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);

        return ResponseEntity.ok(
                java.util.Map.of("message", "User registered successfully")
        );

    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 🔐 Check if account locked
        if (accountLockService.isAccountLocked(user)) {
            throw new RuntimeException("Account locked. Try again after 15 minutes.");
        }

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            accountLockService.loginFailed(user);
            throw new RuntimeException("Invalid credentials");
        }

        // Success
        accountLockService.loginSucceeded(user);

        // generate JWT here
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    // ==============================
    // 🔐 FORGOT PASSWORD
    // ==============================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest().body("New password is required");
        }

        boolean verified = securityQuestionService.verifyAnswer(
                request.getEmail(),
                request.getQuestion(),
                request.getAnswer()
        );

        if (!verified) {
            return ResponseEntity.badRequest()
                    .body("Invalid security answer");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful");
    }

    // ==============================
    // 🔐 RESET TRANSACTION PIN
    // ==============================

    @PostMapping("/reset-pin")
    public ResponseEntity<?> resetPin(
            @RequestBody ResetPinRequest request) {

        boolean verified = securityQuestionService.verifyAnswer(
                request.getEmail(),
                request.getQuestion(),
                request.getAnswer()
        );

        if (!verified) {
            return ResponseEntity.badRequest()
                    .body("Invalid security answer");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        user.setTransactionPin(
                passwordEncoder.encode(request.getNewPin())
        );

        userRepository.save(user);

        return ResponseEntity.ok("PIN reset successful");
    }

    // ==============================
    // 🔐 UNLOCK ACCOUNT
    // ==============================

    @PostMapping("/unlock-account")
    public ResponseEntity<?> unlockAccount(
            @RequestBody UnlockAccountRequest request) {

        boolean verified = securityQuestionService.verifyAnswer(
                request.getEmail(),
                request.getQuestion(),
                request.getAnswer()
        );

        if (!verified) {
            return ResponseEntity.badRequest()
                    .body("Invalid security answer");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        accountLockService.unlockAccount(user);

        return ResponseEntity.ok("Account unlocked successfully");
    }
}