package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.CreateInvoiceDto;
import com.revpayproject.revpay.dto.InvoiceResponse;
import com.revpayproject.revpay.entity.*;
import com.revpayproject.revpay.enums.InvoiceStatus;
import com.revpayproject.revpay.enums.Role;
import com.revpayproject.revpay.enums.TransactionStatus;
import com.revpayproject.revpay.repository.InvoiceRepository;
import com.revpayproject.revpay.repository.TransactionRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final UserService userService;

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

        return "Invoice created successfully. ID: " + invoice.getId();

    }

    @Transactional
    public String payInvoice(Long invoiceId,
                             String payerEmail,
                             String pin) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.SENT
                && invoice.getStatus() != InvoiceStatus.OVERDUE) {
            throw new RuntimeException("Invoice cannot be paid");
        }

        User payer = userRepository.findByEmail(payerEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userService.validatePin(payer, pin);

        Wallet payerWallet = walletRepository.findByUser(payer)
                .orElseThrow(() -> new RuntimeException("Payer wallet not found"));

        Wallet businessWallet = walletRepository.findByUser(invoice.getBusinessUser())
                .orElseThrow(() -> new RuntimeException("Business wallet not found"));

        if (payerWallet.getBalance().compareTo(invoice.getTotalAmount()) < 0) {
            throw new RuntimeException("Insufficient balance to pay invoice");
        }

        // Deduct from payer
        payerWallet.setBalance(
                payerWallet.getBalance().subtract(invoice.getTotalAmount())
        );

        // Credit business
        businessWallet.setBalance(
                businessWallet.getBalance().add(invoice.getTotalAmount())
        );

        // Update invoice status
        invoice.setStatus(InvoiceStatus.PAID);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(invoice.getTotalAmount());
        transaction.setType("INVOICE_PAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(payer);
        transaction.setReceiver(invoice.getBusinessUser());

        transactionRepository.save(transaction);

        // Notifications
        notificationService.createNotification(
                invoice.getBusinessUser(),
                "Invoice paid: ₹" + invoice.getTotalAmount(),
                "INVOICE"
        );

        notificationService.createNotification(
                payer,
                "You paid invoice ₹" + invoice.getTotalAmount(),
                "INVOICE"
        );

        notificationService.checkLowBalance(payer, payerWallet);

        return "Invoice paid successfully";
    }

    public List<InvoiceResponse> getMyInvoices(String email) {

        return invoiceRepository.findByBusinessUser_Email(email)
                .stream()
                .map(invoice -> InvoiceResponse.builder()
                        .id(invoice.getId())
                        .customerName(invoice.getCustomerName())
                        .customerEmail(invoice.getCustomerEmail())
                        .totalAmount(invoice.getTotalAmount())
                        .dueDate(invoice.getDueDate())
                        .status(invoice.getStatus().name())
                        .build())
                .toList();
    }

    @Transactional
    public String sendInvoice(Long invoiceId, String email) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (!invoice.getBusinessUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access");
        }

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT invoices can be sent");
        }

        invoice.setStatus(InvoiceStatus.SENT);

        notificationService.createNotification(
                invoice.getBusinessUser(),
                "Invoice sent to " + invoice.getCustomerEmail(),
                "INVOICE"
        );

        return "Invoice sent successfully";
    }

    public Page<InvoiceResponse> getMyInvoicesPaginated(
            String email,
            Pageable pageable
    ) {
        return invoiceRepository
                .findByBusinessUser_Email(email, pageable)
                .map(invoice -> InvoiceResponse.builder()
                        .id(invoice.getId())
                        .customerName(invoice.getCustomerName())
                        .customerEmail(invoice.getCustomerEmail())
                        .totalAmount(invoice.getTotalAmount())
                        .status(invoice.getStatus().name())
                        .dueDate(invoice.getDueDate())
                        .build()
                );
    }
}