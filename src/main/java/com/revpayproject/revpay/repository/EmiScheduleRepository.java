package com.revpayproject.revpay.repository;

import com.revpayproject.revpay.entity.EmiSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {

    List<EmiSchedule> findByLoan_IdOrderByEmiNumberAsc(Long loanId);

    Page<EmiSchedule> findByLoan_IdOrderByEmiNumberAsc(
            Long loanId,
            Pageable pageable
    );

}