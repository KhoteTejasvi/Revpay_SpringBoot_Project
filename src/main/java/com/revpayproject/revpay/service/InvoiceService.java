package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.CreateInvoiceDto;
import com.revpayproject.revpay.entity.Invoice;
import com.revpayproject.revpay.entity.InvoiceItem;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.enums.InvoiceStatus;
import com.revpayproject.revpay.enums.Role;
import com.revpayproject.revpay.repository.InvoiceRepository;
import com.revpayproject.revpay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;

    public String createInvoice(String email, CreateInvoiceDto dto) {

        User businessUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (businessUser.getRole() != Role.BUSINESS) {
            throw new RuntimeException("Only BUSINESS users can create invoices");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Invoice must contain at least one item");
        }

        Invoice invoice = new Invoice();
        invoice.setCustomerName(dto.getCustomerName());
        invoice.setCustomerEmail(dto.getCustomerEmail());
        invoice.setCustomerAddress(dto.getCustomerAddress());
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setDueDate(dto.getDueDate());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setBusinessUser(businessUser);

        BigDecimal total = BigDecimal.ZERO;
        List<InvoiceItem> items = new ArrayList<>();

        for (var itemDto : dto.getItems()) {

            InvoiceItem item = new InvoiceItem();
            item.setDescription(itemDto.getDescription());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            item.setTax(itemDto.getTax());
            item.setInvoice(invoice);

            BigDecimal itemTotal =
                    itemDto.getUnitPrice()
                            .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                            .add(itemDto.getTax());

            total = total.add(itemTotal);

            items.add(item);
        }

        invoice.setItems(items);
        invoice.setTotalAmount(total);

        invoiceRepository.save(invoice);

        return "Invoice created successfully";
    }
}