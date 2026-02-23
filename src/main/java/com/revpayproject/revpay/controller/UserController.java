package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.CreateBusinessDto;
import com.revpayproject.revpay.dto.NotificationPreferenceDto;
import com.revpayproject.revpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
}