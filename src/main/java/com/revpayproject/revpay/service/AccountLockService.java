package com.revpayproject.revpay.security;

import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountLockService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    private final UserRepository userRepository;

    /**
     * Called when login fails (wrong password)
     */
    public void loginFailed(User user) {

        int newFailedAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailedAttempts);

        if (newFailedAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
        }

        userRepository.save(user);
    }

    /**
     * Called when login succeeds
     */
    public void loginSucceeded(User user) {
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
    }

    /**
     * Check if lock duration expired and unlock automatically
     */
    public boolean unlockWhenTimeExpired(User user) {

        if (user.getLockTime() == null) {
            return false;
        }

        LocalDateTime unlockTime =
                user.getLockTime().plusMinutes(LOCK_DURATION_MINUTES);

        if (unlockTime.isBefore(LocalDateTime.now())) {

            user.setAccountLocked(false);
            user.setFailedAttempts(0);
            user.setLockTime(null);

            userRepository.save(user);
            return true;
        }

        return false;
    }

    /**
     * Manual unlock (Admin feature - optional)
     */
    public void unlockAccount(User user) {
        user.setAccountLocked(false);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    /**
     * Check if account is currently locked
     */
    public boolean isAccountLocked(User user) {

        if (!user.isAccountLocked()) {
            return false;
        }

        // Try auto unlock
        return !unlockWhenTimeExpired(user);
    }
}