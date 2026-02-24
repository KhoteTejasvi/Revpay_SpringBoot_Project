package com.revpayproject.revpay.util;

import com.revpayproject.revpay.entity.Transaction;

import java.io.PrintWriter;
import java.util.List;

public class CsvExportUtil {

    public static void writeTransactions(PrintWriter writer,
                                         List<Transaction> transactions) {

        writer.println("ID,Amount,Type,Status,CreatedAt");

        for (Transaction t : transactions) {
            writer.println(
                    t.getId() + "," +
                            t.getAmount() + "," +
                            t.getType() + "," +
                            t.getStatus() + "," +
                            t.getCreatedAt()
            );
        }
    }
}