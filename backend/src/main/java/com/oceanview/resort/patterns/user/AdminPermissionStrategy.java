package com.oceanview.resort.patterns.user;

import com.oceanview.resort.domain.User;

public class AdminPermissionStrategy implements PermissionStrategy {
    @Override
    public boolean canAccess(String resource, String action, User user) {
        return user != null && "ADMIN".equals(user.getRole() != null ? user.getRole().getCode() : null);
    }
}
