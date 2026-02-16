package com.oceanview.resort.patterns.user;

import com.oceanview.resort.domain.User;

public class CustomerPermissionStrategy implements PermissionStrategy {
    private static final java.util.Set<String> CUSTOMER_RESOURCES = java.util.Set.of("profile", "bookings", "rooms");

    @Override
    public boolean canAccess(String resource, String action, User user) {
        if (user == null || user.getRole() == null) return false;
        return "CUSTOMER".equals(user.getRole().getCode()) && CUSTOMER_RESOURCES.contains(resource);
    }
}
