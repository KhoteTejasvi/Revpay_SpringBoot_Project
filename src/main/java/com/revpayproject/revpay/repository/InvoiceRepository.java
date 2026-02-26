package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.Invoice;
import com.revpayproject.revpay.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Existing methods
    List<Invoice> findByBusinessUser_Email(String email);

    long countByBusinessUser_EmailAndStatus(
            String email,
            InvoiceStatus status
    );

    Page<Invoice> findByBusinessUser_Email(
            String email,
            Pageable pageable
    );

    List<Invoice> findByStatusAndDueDateBefore(
            InvoiceStatus status,
            LocalDateTime dateTime
    );


}