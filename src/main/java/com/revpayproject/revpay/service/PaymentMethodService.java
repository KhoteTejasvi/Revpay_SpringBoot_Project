package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.AddCardDto;
import com.revpayproject.revpay.entity.PaymentMethod;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.PaymentMethodRepository;
import com.revpayproject.revpay.repository.UserRepository;
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

        PaymentMethod card = new PaymentMethod();
        card.setCardNumber(dto.getCardNumber());
        card.setExpiry(dto.getExpiry());
        card.setCvv(dto.getCvv());
        card.setBillingAddress(dto.getBillingAddress());
        card.setUser(user);
        card.setDefault(false);

        paymentMethodRepository.save(card);

        return "Card Added Successfully";
    }

    public List<PaymentMethod> getUserCards(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return paymentMethodRepository.findByUser(user);
    }

    public String deleteCard(Long id) {
        paymentMethodRepository.deleteById(id);
        return "Card Deleted Successfully";
    }
}