package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.CreateInvoiceDto;
import com.revpayproject.revpay.entity.Invoice;
import com.revpayproject.revpay.repository.InvoiceRepository;
import com.revpayproject.revpay.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @PostMapping("/create")
    public String createInvoice(@RequestBody CreateInvoiceDto dto) {
        return invoiceService.createInvoice(
                getLoggedInEmail(),
                dto
        );
    }

    @GetMapping("/all")
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}