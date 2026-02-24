package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.service.TransactionService;
import com.revpayproject.revpay.util.CsvExportUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<Transaction> transactions =
                transactionService.getUserTransactions(email);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=transactions.csv");

        PrintWriter writer = response.getWriter();
        CsvExportUtil.writeTransactions(writer, transactions);
        writer.flush();
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws Exception {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<Transaction> transactions =
                transactionService.getUserTransactions(email);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=transactions.pdf");

        com.itextpdf.kernel.pdf.PdfWriter writer =
                new com.itextpdf.kernel.pdf.PdfWriter(response.getOutputStream());

        com.itextpdf.kernel.pdf.PdfDocument pdf =
                new com.itextpdf.kernel.pdf.PdfDocument(writer);

        com.itextpdf.layout.Document document =
                new com.itextpdf.layout.Document(pdf);

        document.add(new com.itextpdf.layout.element.Paragraph("Transaction Report"));

        for (Transaction t : transactions) {
            document.add(new com.itextpdf.layout.element.Paragraph(
                    "ID: " + t.getId() +
                            " | Amount: " + t.getAmount() +
                            " | Type: " + t.getType() +
                            " | Status: " + t.getStatus()
            ));
        }

        document.close();
    }
}
