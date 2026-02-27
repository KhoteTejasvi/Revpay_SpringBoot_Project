package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.CreateBusinessDto;
import com.revpayproject.revpay.dto.NotificationPreferenceDto;
import com.revpayproject.revpay.dto.RegisterRequest;
import com.revpayproject.revpay.entity.BusinessProfile;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import com.revpayproject.revpay.enums.Role;
import com.revpayproject.revpay.repository.BusinessProfileRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    public String updateNotificationPreferences(String email,
                                                NotificationPreferenceDto dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setTransactionNotifications(dto.isTransactionNotifications());
        user.setEmailNotifications(dto.isEmailNotifications());
        user.setLowBalanceNotifications(dto.isLowBalanceNotifications());

        userRepository.save(user);

        return "Notification preferences updated successfully";
    }

    @Transactional
    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 🔥 Role logic
        if ("BUSINESS".equalsIgnoreCase(request.getRole())) {
            user.setRole(Role.BUSINESS);

            BusinessProfile profile = new BusinessProfile();
            profile.setBusinessName(request.getBusinessName());
            profile.setBusinessType(request.getBusinessType());
            profile.setTaxId(request.getTaxId());
            profile.setBusinessAddress(request.getBusinessAddress());
            profile.setUser(user);

            businessProfileRepository.save(profile);

        } else {
            user.setRole(Role.USER);
        }

        // ✅ SAVE USER FIRST
        User savedUser = userRepository.save(user);

        // ✅ CREATE WALLET
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(java.math.BigDecimal.ZERO);

        walletRepository.save(wallet);

        return "User registered successfully";
    }

    public String upgradeToBusiness(String email,CreateBusinessDto dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.BUSINESS) {
            throw new RuntimeException("Already a business account");
        }

        BusinessProfile profile = new BusinessProfile();
        profile.setBusinessName(dto.getBusinessName());
        profile.setBusinessType(dto.getBusinessType());
        profile.setTaxId(dto.getTaxId());
        profile.setBusinessAddress(dto.getBusinessAddress());
        profile.setContactNumber(dto.getContactNumber());
        profile.setUser(user);

        businessProfileRepository.save(profile);

        user.setRole(Role.BUSINESS);
        userRepository.save(user);

        return "Account upgraded to BUSINESS successfully";
    }

    public String setTransactionPin(String email, String pin) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (pin.length() != 4) {
            throw new RuntimeException("PIN must be 4 digits");
        }

        user.setTransactionPin(passwordEncoder.encode(pin));

        userRepository.save(user);

        return "Transaction PIN set successfully";
    }

    public void validatePin(User user, String rawPin) {

        if (user.getTransactionPin() == null) {
            throw new RuntimeException("Transaction PIN not set");
        }

        if (!passwordEncoder.matches(rawPin, user.getTransactionPin())) {
            throw new RuntimeException("Invalid Transaction PIN");
        }
    }

    public User getLoggedInUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}