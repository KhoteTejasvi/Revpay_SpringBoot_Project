package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.AddMoneyRequest;
import com.revpayproject.revpay.dto.TransactionResponse;
import com.revpayproject.revpay.dto.TransferRequest;
import com.revpayproject.revpay.entity.PaymentMethod;
import com.revpayproject.revpay.entity.Transaction;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.entity.Wallet;
import com.revpayproject.revpay.repository.PaymentMethodRepository;
import com.revpayproject.revpay.repository.TransactionRepository;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.revpayproject.revpay.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.revpayproject.revpay.specification.TransactionSpecification;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final NotificationService notificationService;
    private static final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal("500");

    public BigDecimal getBalance(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return wallet.getBalance();
    }

    public String addMoney(String email, AddMoneyRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);

        return "Money Added Successfully";
    }

    @Transactional
    public String transferMoney(String senderEmail, TransferRequest request) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType("TRANSFER");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {

            transaction.setStatus(TransactionStatus.FAILED);            transactionRepository.save(transaction);

            throw new RuntimeException("Insufficient Balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));

        notificationService.checkLowBalance(sender, senderWallet);

        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return "Transfer Successful";
    }

    public List<TransactionResponse> getTransactions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions =
                transactionRepository.findBySenderOrReceiver(user, user);

        return transactions.stream()
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getStatus(),
                        tx.getCreatedAt(),
                        tx.getSender().getEmail(),
                        tx.getReceiver().getEmail()
                ))
                .toList();
    }

    @Transactional
    public String addMoneyFromCard(String email, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        PaymentMethod defaultCard = paymentMethodRepository.findByUser(user)
                .stream()
                .filter(PaymentMethod::isDefault)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No default card found"));


        wallet.setBalance(wallet.getBalance().add(amount));

        notificationService.createNotification(
                user,
                "₹" + amount + " added to your wallet",
                "TRANSACTION"
        );

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType("ADD_MONEY");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(user);
        transaction.setReceiver(user);

        transactionRepository.save(transaction);

        return "Money Added Successfully via Card " + defaultCard.getMaskedCardNumber();
    }

    @Transactional
    public String withdrawMoney(String email, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient Balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        checkLowBalance(user, wallet);
        notificationService.createNotification(
                user,
                "₹" + amount + " withdrawn from wallet",
                "TRANSACTION"
        );

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType("WITHDRAW");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(user);
        transaction.setReceiver(user);

        transactionRepository.save(transaction);

        return "Withdrawal Successful";
    }

    private void checkLowBalance(User user, Wallet wallet) {

        if (user.isLowBalanceNotifications() &&
                wallet.getBalance().compareTo(LOW_BALANCE_THRESHOLD) < 0) {

            notificationService.createNotification(
                    user,
                    "⚠ Your wallet balance is low: ₹" + wallet.getBalance(),
                    "ALERT"
            );
        }
    }

    public Page<Transaction> filterTransactions(
            String email,
            String type,
            TransactionStatus status,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<Transaction> spec =
                Specification.where(TransactionSpecification.hasType(type))
                        .and(TransactionSpecification.hasStatus(status))
                        .and(TransactionSpecification.minAmount(minAmount))
                        .and(TransactionSpecification.maxAmount(maxAmount))
                        .and(TransactionSpecification.startDate(startDate))
                        .and(TransactionSpecification.endDate(endDate));

        // Restrict only user’s transactions
        Specification<Transaction> userSpec = (root, query, cb) ->
                cb.or(
                        cb.equal(root.get("sender"), user),
                        cb.equal(root.get("receiver"), user)
                );

        spec = spec.and(userSpec);

        return transactionRepository.findAll(spec, pageable);
    }

    public void exportTransactions(String email, HttpServletResponse response)
            throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions =
                transactionRepository.findBySenderOrReceiver(user, user);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=transactions.csv");

        PrintWriter writer = response.getWriter();
        CSVWriter csvWriter = new CSVWriter(writer);

        // Header
        String[] header = {
                "ID", "Type", "Status", "Amount",
                "Sender", "Receiver", "Created At"
        };
        csvWriter.writeNext(header);

        // Data
        for (Transaction tx : transactions) {
            String[] data = {
                    String.valueOf(tx.getId()),
                    tx.getType(),
                    tx.getStatus().name(),
                    tx.getAmount().toString(),
                    tx.getSender().getEmail(),
                    tx.getReceiver().getEmail(),
                    tx.getCreatedAt().toString()
            };
            csvWriter.writeNext(data);
        }

        csvWriter.close();
    }
}