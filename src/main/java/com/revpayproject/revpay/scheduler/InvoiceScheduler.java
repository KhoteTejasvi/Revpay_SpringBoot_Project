package com.revpayproject.revpay.scheduler;

import com.revpayproject.revpay.entity.Invoice;
import com.revpayproject.revpay.enums.InvoiceStatus;
import com.revpayproject.revpay.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final InvoiceRepository invoiceRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void markOverdueInvoices() {

        System.out.println("Midnight Scheduler running...");

        List<Invoice> overdueInvoices =
                invoiceRepository.findByStatusAndDueDateBefore(
                        InvoiceStatus.SENT,
                        LocalDateTime.now()
                );

        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            System.out.println("Marked overdue: " + invoice.getId());
        }

        invoiceRepository.saveAll(overdueInvoices);
    }
}