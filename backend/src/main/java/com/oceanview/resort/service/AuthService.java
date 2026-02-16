package com.oceanview.resort.service;

import com.oceanview.resort.domain.User;
import com.oceanview.resort.patterns.user.AccountState;
import com.oceanview.resort.patterns.user.ActiveAccountState;
import com.oceanview.resort.patterns.user.LockedAccountState;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.security.PasswordHasher;
import com.oceanview.resort.repository.UserRepositoryImpl;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final ActivityLogService activityLogService = new ActivityLogService();

    public Optional<User> login(String username, String password, HttpServletRequest request) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) return Optional.empty();
        User user = opt.get();
        AccountState state = user.getLocked() != null && user.getLocked() ? new LockedAccountState() : new ActiveAccountState();
        if (!state.canLogin()) return Optional.empty();
        if (!PasswordHasher.verify(password, user.getPasswordHash())) {
            incrementFailedAttempts(user);
            return Optional.empty();
        }
        user.setFailedAttempts(0);
        user.setLastLoginAt(Instant.now());
        userRepository.update(user);
        activityLogService.log(user.getId(), "LOGIN", "USER", String.valueOf(user.getId()), request.getRemoteAddr());
        return Optional.of(user);
    }

    private void incrementFailedAttempts(User user) {
        int fails = (user.getFailedAttempts() != null ? user.getFailedAttempts() : 0) + 1;
        user.setFailedAttempts(fails);
        if (fails >= 5) {
            user.setLocked(true);
            user.setLockReason("Too many failed attempts");
        }
        userRepository.update(user);
    }
}
