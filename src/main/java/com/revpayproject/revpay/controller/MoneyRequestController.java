package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.MoneyRequestResponse;
import com.revpayproject.revpay.dto.SendRequestDto;
import com.revpayproject.revpay.entity.MoneyRequest;
import com.revpayproject.revpay.enums.RequestStatus;
import com.revpayproject.revpay.service.MoneyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    // Send request
    @PostMapping("/send")
    public String sendRequest(@RequestBody SendRequestDto dto) {
        return moneyRequestService.sendRequest(getLoggedInEmail(), dto);
    }

    // Accept request
    @PutMapping("/{id}/accept")
    public String acceptRequest(@PathVariable Long id) {
        return moneyRequestService.acceptRequest(id, getLoggedInEmail());
    }

    // Decline request
    @PutMapping("/{id}/decline")
    public String declineRequest(@PathVariable Long id) {
        return moneyRequestService.declineRequest(id, getLoggedInEmail());
    }

    // Incoming requests
    @GetMapping("/incoming")
    public List<MoneyRequestResponse> getIncomingRequests() {
        return moneyRequestService.getIncomingRequests(getLoggedInEmail());
    }

    // Outgoing requests
    @GetMapping("/outgoing")
    public List<MoneyRequest> getOutgoingRequests() {
        return moneyRequestService.getOutgoingRequests(getLoggedInEmail());
    }

    @PutMapping("/{id}/cancel")
    public String cancelRequest(@PathVariable Long id) {
        return moneyRequestService.cancelRequest(id, getLoggedInEmail());
    }
}