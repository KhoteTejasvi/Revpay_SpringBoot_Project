package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.AddCardDto;
import com.revpayproject.revpay.dto.EditCardDto;
import com.revpayproject.revpay.dto.PaymentMethodResponse;
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

    public List<PaymentMethodResponse> getUserCards(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return paymentMethodRepository.findByUser(user)
                .stream()
                .map(card -> new PaymentMethodResponse(
                        card.getId(),
                        card.getMaskedCardNumber(),
                        card.getExpiry(),
                        card.isDefault()
                ))
                .toList();
    }

    public String deleteCard(Long id, String email) {

        PaymentMethod card = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        User user = card.getUser();
        boolean wasDefault = card.isDefault();

        // Delete the card
        paymentMethodRepository.delete(card);

        // 🔥 If deleted card was default → assign new default
        if (wasDefault) {

            List<PaymentMethod> remainingCards =
                    paymentMethodRepository.findByUser(user);

            if (!remainingCards.isEmpty()) {
                remainingCards.get(0).setDefault(true);
                paymentMethodRepository.save(remainingCards.get(0));
            }
        }

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

    public String editCard(Long cardId, String email, EditCardDto dto) {

        PaymentMethod card = paymentMethodRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        card.setExpiry(dto.getExpiry());
        card.setBillingAddress(dto.getBillingAddress());

        paymentMethodRepository.save(card);

        return "Card updated successfully";
    }

    public String setDefaultCard(Long cardId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PaymentMethod> userCards =
                paymentMethodRepository.findByUser(user);

        PaymentMethod selectedCard = userCards.stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card not found or unauthorized"));

        // Remove default from all cards
        for (PaymentMethod card : userCards) {
            card.setDefault(false);
        }

        // Set selected as default
        selectedCard.setDefault(true);

        paymentMethodRepository.saveAll(userCards);

        return "Default card updated successfully";
    }

}