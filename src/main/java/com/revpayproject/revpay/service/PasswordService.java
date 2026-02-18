package com.revpayproject.revpay.service;

import com.revpayproject.revpay.entity.Password;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordRepository passwordRepository;

    public Password savePassword(Password password) {
        return passwordRepository.save(password);
    }

    public List<Password> getUserPasswords(User user) {
        return passwordRepository.findByUser(user);
    }

    public void deletePassword(Long id) {
        passwordRepository.deleteById(id);
    }
}
