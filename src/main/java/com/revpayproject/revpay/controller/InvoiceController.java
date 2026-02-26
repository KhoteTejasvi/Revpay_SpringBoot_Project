package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.CreateInvoiceDto;
import com.revpayproject.revpay.dto.InvoiceResponse;
import com.revpayproject.revpay.dto.TransactionPinDto;
import com.revpayproject.revpay.entity.Invoice;
import com.revpayproject.revpay.repository.InvoiceRepository;
import com.revpayproject.revpay.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PostMapping("/{id}/pay")
    public String payInvoice(@PathVariable Long id,
                             @RequestBody TransactionPinDto dto) {

        return invoiceService.payInvoice(
                id,
                getLoggedInEmail(),
                dto.getPin()
        );
    }

    @PostMapping("/{id}/send")
    public String sendInvoice(@PathVariable Long id) {
        return invoiceService.sendInvoice(id, getLoggedInEmail());
    }

    @GetMapping("/my")
    public Page<InvoiceResponse> getMyInvoices(Pageable pageable) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return invoiceService.getMyInvoicesPaginated(email, pageable);
    }

    @GetMapping("/all")
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @PostMapping("/{id}/cancel")
    public String cancelInvoice(@PathVariable Long id) {
        return invoiceService.cancelInvoice(id, getLoggedInEmail());
    }

    @PostMapping("/{id}/mark-paid-manual")
    public String markPaidManually(@PathVariable Long id) {
        return invoiceService.markInvoiceAsPaidManually(id, getLoggedInEmail());
    }

}