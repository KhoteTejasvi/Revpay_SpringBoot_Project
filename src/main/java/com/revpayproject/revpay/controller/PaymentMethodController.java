package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.AddCardDto;
import com.revpayproject.revpay.dto.PaymentMethodResponse;
import com.revpayproject.revpay.entity.PaymentMethod;
import com.revpayproject.revpay.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @PostMapping("/add")
    public String addCard(@RequestBody AddCardDto dto) {
        return paymentMethodService.addCard(getLoggedInEmail(), dto);
    }

    @GetMapping("/my-cards")
    public List<PaymentMethodResponse> getCards() {
        return paymentMethodService.getUserCards(getLoggedInEmail());
    }

    @DeleteMapping("/{id}")
    public String deleteCard(@PathVariable Long id) {
        return paymentMethodService.deleteCard(id, getLoggedInEmail());
    }
}