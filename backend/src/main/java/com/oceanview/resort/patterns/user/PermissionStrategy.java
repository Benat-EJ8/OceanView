package com.oceanview.resort.patterns.user;

import com.oceanview.resort.domain.User;

/**
 * Strategy: Role-based permission check.
 */
public interface PermissionStrategy {
    boolean canAccess(String resource, String action, User user);
}
