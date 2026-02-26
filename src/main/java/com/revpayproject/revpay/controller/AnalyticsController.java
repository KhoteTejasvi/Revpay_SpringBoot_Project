package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.PaymentTrendResponse;
import com.revpayproject.revpay.dto.RevenueResponse;
import com.revpayproject.revpay.dto.TopCustomerResponse;
import com.revpayproject.revpay.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.revpayproject.revpay.analytics.dto.ChartPointDTO;
import org.springframework.http.ResponseEntity;



import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/revenue")
    public RevenueResponse getRevenue(@RequestParam String period) {
        return analyticsService.getRevenue(period);
    }

    @PreAuthorize("hasRole('BUSINESS')")
    @GetMapping("/top-customers")
    public List<TopCustomerResponse> getTopCustomers() {
        return analyticsService.getTopCustomers();
    }

    @PreAuthorize("hasRole('BUSINESS')")
    @GetMapping("/payment-trends")
    public List<PaymentTrendResponse> getPaymentTrends(
            @RequestParam(defaultValue = "7") int days) {
        return analyticsService.getPaymentTrends(days);
    }

    @PreAuthorize("hasRole('BUSINESS')")
    @GetMapping("/revenue/daily")
    public ResponseEntity<List<ChartPointDTO>> dailyRevenue() {
        return ResponseEntity.ok(
                analyticsService.getDailyRevenueChart()
        );
    }

    @PreAuthorize("hasRole('BUSINESS')")
    @GetMapping("/revenue/weekly")
    public ResponseEntity<List<ChartPointDTO>> weeklyRevenue() {
        return ResponseEntity.ok(
                analyticsService.getWeeklyRevenueChart()
        );
    }

    @PreAuthorize("hasRole('BUSINESS')")
    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<ChartPointDTO>> monthlyRevenue() {
        return ResponseEntity.ok(
                analyticsService.getMonthlyRevenueChart()
        );
    }
}