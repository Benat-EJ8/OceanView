package com.oceanview.resort.security;

import com.oceanview.resort.domain.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages HTTP session and optional server-side session store.
 * Session timeout is configured in application.properties.
 */
public final class SessionManager {
    private static final String ATTR_USER = "authUser";
    private static final String ATTR_USER_ID = "authUserId";
    private static final String ATTR_ROLE = "authRole";
    private static final int DEFAULT_TIMEOUT_MINUTES = 30;

    private final int sessionTimeoutSeconds;

    public SessionManager(int sessionTimeoutMinutes) {
        this.sessionTimeoutSeconds = Math.max(1, sessionTimeoutMinutes) * 60;
    }

    public static SessionManager getInstance() {
        return Holder.INSTANCE;
    }

    public void createSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(sessionTimeoutSeconds);
        session.setAttribute(ATTR_USER_ID, user.getId());
        session.setAttribute(ATTR_USER, user);
        session.setAttribute(ATTR_ROLE, user.getRole() != null ? user.getRole().getCode() : null);
    }

    public User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object u = session.getAttribute(ATTR_USER);
        return u instanceof User ? (User) u : null;
    }

    public Integer getCurrentUserId(HttpServletRequest request) {
        User u = getCurrentUser(request);
        return u != null ? u.getId() : null;
    }

    public String getCurrentRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object r = session.getAttribute(ATTR_ROLE);
        return r != null ? r.toString() : null;
    }

    public void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    private static class Holder {
        private static final SessionManager INSTANCE = new SessionManager(DEFAULT_TIMEOUT_MINUTES);
    }
}
