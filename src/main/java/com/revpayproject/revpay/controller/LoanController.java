package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.ApplyLoanDto;
import com.revpayproject.revpay.dto.EmiScheduleResponse;
import com.revpayproject.revpay.dto.LoanResponse;
import com.revpayproject.revpay.dto.TransactionPinDto;
import com.revpayproject.revpay.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @PostMapping("/apply")
    public String applyLoan(@RequestBody ApplyLoanDto dto) {
        return loanService.applyLoan(getLoggedInEmail(), dto);
    }

//    @GetMapping("/my")
//    public List<com.revpayproject.revpay.dto.LoanResponse> getMyLoans() {
//        return loanService.getMyLoans(getLoggedInEmail());
//    }

    @PostMapping("/{id}/repay")
    public String repayLoan(@PathVariable Long id,
                            @RequestBody TransactionPinDto dto) {

        return loanService.repayLoan(
                id,
                getLoggedInEmail(),
                dto.getPin()
        );
    }

    @GetMapping("/my")
    public Page<LoanResponse> getMyLoans(Pageable pageable) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return loanService.getMyLoansPaginated(email, pageable);
    }

    @GetMapping("/{loanId}/schedule")
    public Page<EmiScheduleResponse> getEmiSchedule(
            @PathVariable Long loanId,
            Pageable pageable) {

        return loanService.getSchedule(loanId, pageable);
    }

    @PutMapping("/{loanId}/reject")
    public ResponseEntity<String> rejectLoan(
            @PathVariable Long loanId,
            @RequestParam String reason) {

        loanService.rejectLoan(loanId, reason);
        return ResponseEntity.ok("Loan Rejected");
    }

    @PostMapping("/{loanId}/upload-document")
    public ResponseEntity<String> uploadDocument(
            @PathVariable Long loanId,
            @RequestParam MultipartFile file) {

        loanService.uploadDocument(loanId, file);
        return ResponseEntity.ok("Document Uploaded Successfully");
    }

}