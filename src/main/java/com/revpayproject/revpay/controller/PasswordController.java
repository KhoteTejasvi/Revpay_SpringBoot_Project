package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.entity.Password;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;
    private final UserRepository userRepository;

    @PostMapping
    public Password savePassword(@RequestBody Password password,
                                 Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        password.setUser(user);

        return passwordService.savePassword(password);
    }

    @GetMapping
    public List<Password> getPasswords(Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        return passwordService.getUserPasswords(user);
    }

    @DeleteMapping("/{id}")
    public void deletePassword(@PathVariable Long id) {
        passwordService.deletePassword(id);
    }
}

