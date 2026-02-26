package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.MoneyRequestResponse;
import com.revpayproject.revpay.dto.SendRequestDto;
import com.revpayproject.revpay.entity.*;
import com.revpayproject.revpay.enums.RequestStatus;
import com.revpayproject.revpay.enums.TransactionStatus;
import com.revpayproject.revpay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
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

        if (dto.getAmount() == null ||
                dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByEmail(dto.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getEmail().equals(receiver.getEmail())) {
            throw new RuntimeException("You cannot request money from yourself");
        }

        MoneyRequest request = new MoneyRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAmount(dto.getAmount());
        request.setNote(dto.getNote());
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        moneyRequestRepository.save(request);

        // Notify receiver
        notificationService.createNotification(
                receiver,
                "New money request of ₹" + dto.getAmount() +
                        " from " + sender.getFullName(),
                "REQUEST"
        );

        return "Request Sent Successfully";
    }

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

        receiverWallet.setBalance(
                receiverWallet.getBalance().subtract(request.getAmount())
        );

        senderWallet.setBalance(
                senderWallet.getBalance().add(request.getAmount())
        );

        walletRepository.save(receiverWallet);
        walletRepository.save(senderWallet);

        request.setStatus(RequestStatus.ACCEPTED);
        moneyRequestRepository.save(request);

        notificationService.checkLowBalance(request.getReceiver(), receiverWallet);

        notificationService.createNotification(
                request.getSender(),
                "Your money request of ₹" + request.getAmount() + " was accepted",
                "REQUEST"
        );

        notificationService.createNotification(
                request.getReceiver(),
                "You accepted a request of ₹" + request.getAmount(),
                "REQUEST"
        );

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType("REQUEST_PAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSender(request.getReceiver());   // Paid by receiver
        transaction.setReceiver(request.getSender());   // Received by sender

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

        notificationService.createNotification(
                request.getSender(),
                "Your money request of ₹" + request.getAmount() + " was declined",
                "REQUEST"
        );

        return "Request Declined";
    }

    // INCOMING REQUESTS
    public List<MoneyRequestResponse> getIncomingRequests(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return moneyRequestRepository.findByReceiver(user)
                .stream()
                .map(r -> new MoneyRequestResponse(
                        r.getId(),
                        r.getAmount(),
                        r.getNote(),
                        r.getStatus().name(),
                        r.getCreatedAt(),
                        r.getSender().getEmail(),
                        r.getReceiver().getEmail()
                ))
                .toList();
    }

    // OUTGOING REQUESTS
    public List<MoneyRequestResponse> getOutgoingRequests(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return moneyRequestRepository.findBySender(user)
                .stream()
                .map(r -> new MoneyRequestResponse(
                        r.getId(),
                        r.getAmount(),
                        r.getNote(),
                        r.getStatus().name(),
                        r.getCreatedAt(),
                        r.getSender().getEmail(),
                        r.getReceiver().getEmail()
                ))
                .toList();
    }

    public String cancelRequest(Long requestId, String senderEmail) {

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getSender().getEmail().equals(senderEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        request.setStatus(RequestStatus.CANCELLED);
        moneyRequestRepository.save(request);

        return "Request Cancelled Successfully";
    }

    public List<MoneyRequestResponse> getRequestsByStatus(String email, RequestStatus status) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return moneyRequestRepository.findByReceiverAndStatus(user, status)
                .stream()
                .map(r -> new MoneyRequestResponse(
                        r.getId(),
                        r.getAmount(),
                        r.getNote(),
                        r.getStatus().name(),
                        r.getCreatedAt(),
                        r.getSender().getEmail(),
                        r.getReceiver().getEmail()
                ))
                .toList();
    }
}