package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.AddCardDto;
import com.revpayproject.revpay.entity.PaymentMethod;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.PaymentMethodRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.security.CardEncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public String addCard(String email, AddCardDto dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!isValidCardNumber(dto.getCardNumber())) {
            throw new RuntimeException("Invalid Card Number");
        }

        String last4 = dto.getCardNumber()
                .substring(dto.getCardNumber().length() - 4);

        String masked = "**** **** **** " + last4;

        PaymentMethod card = new PaymentMethod();
        card.setMaskedCardNumber(masked);
        card.setLast4Digits(last4);
        card.setExpiry(dto.getExpiry());
        card.setEncryptedCvv(
                CardEncryptionUtil.encrypt(dto.getCvv())
        );
        card.setBillingAddress(dto.getBillingAddress());
        card.setUser(user);

        // If first card, make default
        if (paymentMethodRepository.findByUser(user).isEmpty()) {
            card.setDefault(true);
        } else {
            card.setDefault(false);
        }

        paymentMethodRepository.save(card);

        return "Card Added Securely";
    }

    public List<PaymentMethod> getUserCards(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return paymentMethodRepository.findByUser(user);
    }

    public String deleteCard(Long id, String email) {

        PaymentMethod card = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        paymentMethodRepository.delete(card);

        return "Card Deleted";
    }

    private boolean isValidCardNumber(String cardNumber) {

        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }

            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}