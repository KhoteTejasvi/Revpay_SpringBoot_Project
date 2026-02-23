package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.Invoice;
import com.revpayproject.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByBusinessUser(User user);
}