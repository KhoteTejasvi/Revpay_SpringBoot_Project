package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.CreateBusinessDto;
import com.revpayproject.revpay.dto.NotificationPreferenceDto;
import com.revpayproject.revpay.dto.SetPinDto;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;


    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @PutMapping("/notification-preferences")
    public String updatePreferences(
            @RequestBody NotificationPreferenceDto dto) {

        return userService.updateNotificationPreferences(
                getLoggedInEmail(),
                dto
        );
    }

    @PostMapping("/upgrade-business")
    public String upgradeToBusiness(
            @RequestBody CreateBusinessDto dto) {

        return userService.upgradeToBusiness(
                getLoggedInEmail(),
                dto
        );
    }

    @PostMapping("/set-pin")
    public String setPin(@RequestBody SetPinDto dto) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userService.setTransactionPin(email, dto.getPin());
    }

    @GetMapping("/profile")
    public User getProfile() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}