package com.oceanview.resort.patterns.user;

import com.oceanview.resort.domain.User;

public class ReceptionistPermissionStrategy implements PermissionStrategy {
    private static final java.util.Set<String> RECEPTIONIST_RESOURCES = java.util.Set.of(
            "rooms", "reservations", "guests", "checkin", "checkout", "billing"
    );

    @Override
    public boolean canAccess(String resource, String action, User user) {
        if (user == null || user.getRole() == null) return false;
        if (!"RECEPTIONIST".equals(user.getRole().getCode())) return false;
        return RECEPTIONIST_RESOURCES.contains(resource);
    }
}
