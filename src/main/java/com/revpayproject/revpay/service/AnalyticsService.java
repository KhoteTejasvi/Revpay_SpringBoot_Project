package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.RevenueResponse;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public RevenueResponse getRevenue(String period) {

        User user = userService.getLoggedInUser();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        switch (period.toLowerCase()) {

            case "daily":
                start = now.minusDays(1);
                break;

            case "weekly":
                start = now.minusWeeks(1);
                break;

            case "monthly":
                start = now.minusMonths(1);
                break;

            default:
                throw new RuntimeException("Invalid period. Use daily, weekly, or monthly.");
        }

        BigDecimal revenue =
                transactionRepository.getRevenueBetweenDates(
                        user.getId(), start, now);

        return new RevenueResponse(period, revenue);
    }
}