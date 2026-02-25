package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.PaymentTrendResponse;
import com.revpayproject.revpay.dto.RevenueResponse;
import com.revpayproject.revpay.dto.TopCustomerResponse;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.stream.Collectors;
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

    public List<TopCustomerResponse> getTopCustomers() {

        User user = userService.getLoggedInUser();

        List<Object[]> results =
                transactionRepository.findTopCustomers(
                        user.getId(),
                        PageRequest.of(0, 5)   // Top 5 customers
                );

        return results.stream()
                .map(obj -> new TopCustomerResponse(
                        (String) obj[0],
                        (java.math.BigDecimal) obj[1]
                ))
                .collect(Collectors.toList());
    }

    public List<PaymentTrendResponse> getPaymentTrends(int days) {

        User user = userService.getLoggedInUser();

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(days);

        List<Object[]> results =
                transactionRepository.getDailyRevenue(
                        user.getId(), start, end);

        return results.stream()
                .map(obj -> new PaymentTrendResponse(
                        ((java.sql.Date) obj[0]).toLocalDate(),
                        (java.math.BigDecimal) obj[1]
                ))
                .collect(Collectors.toList());
    }
}