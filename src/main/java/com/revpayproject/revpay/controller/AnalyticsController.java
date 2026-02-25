package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.RevenueResponse;
import com.revpayproject.revpay.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/revenue")
    public RevenueResponse getRevenue(
            @RequestParam String period) {

        return analyticsService.getRevenue(period);
    }
}