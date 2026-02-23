package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.SendRequestDto;
import com.revpayproject.revpay.entity.*;
import com.revpayproject.revpay.enums.RequestStatus;
import com.revpayproject.revpay.enums.TransactionStatus;
import com.revpayproject.revpay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoneyRequestService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final MoneyRequestRepository moneyRequestRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    public String sendRequest(String senderEmail, SendRequestDto dto) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByEmail(dto.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        MoneyRequest request = new MoneyRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAmount(dto.getAmount());
        request.setNote(dto.getNote());
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        moneyRequestRepository.save(request);

        return "Request Sent Successfully";
    }

    // ACCEPT REQUEST
    @Transactional
    public String acceptRequest(Long requestId, String receiverEmail) {

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getReceiver().getEmail().equals(receiverEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        Wallet receiverWallet = walletRepository.findByUser(request.getReceiver())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        Wallet senderWallet = walletRepository.findByUser(request.getSender())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        if (receiverWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient Balance");
        }

        // Deduct from receiver
        receiverWallet.setBalance(
                receiverWallet.getBalance().subtract(request.getAmount())
        );

        // Add to sender
        senderWallet.setBalance(
                senderWallet.getBalance().add(request.getAmount())
        );

        // Update request status
        request.setStatus(RequestStatus.ACCEPTED);

        // Notify sender
        notificationService.createNotification(
                request.getSender(),
                "Your money request of ₹" + request.getAmount() + " was accepted",
                "REQUEST"
        );

        // Notify receiver
        notificationService.createNotification(
                request.getReceiver(),
                "You accepted a request of ₹" + request.getAmount(),
                "REQUEST"
        );

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType("REQUEST_PAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(request.getReceiver());   // Money paid by receiver
        transaction.setReceiver(request.getSender());   // Money received by sender

        transactionRepository.save(transaction);

        return "Request Accepted Successfully";
    }

    // DECLINE REQUEST
    public String declineRequest(Long requestId, String receiverEmail) {

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getReceiver().getEmail().equals(receiverEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        request.setStatus(RequestStatus.DECLINED);
        moneyRequestRepository.save(request);

        return "Request Declined";
    }

    // INCOMING REQUESTS
    public List<MoneyRequest> getIncomingRequests(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return moneyRequestRepository.findByReceiver(user);
    }

    // OUTGOING REQUESTS
    public List<MoneyRequest> getOutgoingRequests(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return moneyRequestRepository.findBySender(user);
    }
}