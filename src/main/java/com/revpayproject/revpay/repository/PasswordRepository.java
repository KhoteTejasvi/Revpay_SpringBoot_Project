package com.revpayproject.revpay.repository;


import com.revpayproject.revpay.entity.Password;
import com.revpayproject.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {

    List<Password> findByUser(User user);

}
