package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByBusinessUser_Email(String email);

    long countByBusinessUser_EmailAndStatus(
            String email,
            com.revpayproject.revpay.enums.InvoiceStatus status);
}