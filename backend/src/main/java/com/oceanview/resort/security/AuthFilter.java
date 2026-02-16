package com.oceanview.resort.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class AuthFilter implements Filter {
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/room-categories",
            "/api/rooms");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Handle CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String path = req.getServletPath();
        // Fallback if getServletPath() returns empty or "/"
        if (path == null || path.isEmpty()) {
            path = req.getRequestURI();
            String ctx = req.getContextPath();
            if (ctx != null && !ctx.isEmpty() && path.startsWith(ctx)) {
                path = path.substring(ctx.length());
            }
        }

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (SessionManager.getInstance().getCurrentUser(req) == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false,\"message\":\"Not authenticated\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (path == null)
            return false;
        // Exact matches
        if (PUBLIC_PATHS.contains(path))
            return true;

        // Prefix matches
        if (path.startsWith("/api/room-categories"))
            return true;
        if (path.startsWith("/api/rooms"))
            return true;
        if (path.startsWith("/api/auth/login"))
            return true;
        if (path.startsWith("/api/auth/register"))
            return true;

        return false;
    }
}
