package com.oceanview.resort.patterns.user;

import com.oceanview.resort.domain.User;

public class ManagerPermissionStrategy implements PermissionStrategy {
    private static final java.util.Set<String> MANAGER_RESOURCES = java.util.Set.of(
            "reports", "staff", "receptionists", "users", "rooms", "reservations", "billing", "guests", "maintenance"
    );

    @Override
    public boolean canAccess(String resource, String action, User user) {
        if (user == null || user.getRole() == null) return false;
        if (!"MANAGER".equals(user.getRole().getCode())) return false;
        return MANAGER_RESOURCES.contains(resource);
    }
}
