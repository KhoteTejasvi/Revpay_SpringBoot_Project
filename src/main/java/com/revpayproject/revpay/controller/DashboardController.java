package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.DashboardSummaryResponse;
import com.revpayproject.revpay.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getDashboardSummary();
    }
}