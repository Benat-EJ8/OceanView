package com.oceanview.resort.controller;

import com.oceanview.resort.domain.Notification;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.service.NotificationService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NotificationServlet extends HttpServlet {
    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            Integer userId = SessionManager.getInstance().getCurrentUserId(req);
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Not authenticated")));
                return;
            }
            String unreadCount = req.getParameter("unreadCount");
            if ("true".equals(unreadCount)) {
                int count = notificationService.countUnread(userId);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(Map.of("count", count))));
                return;
            }
            List<Notification> list = notificationService.findByUserId(userId);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String idParam = req.getParameter("id");
            String action = req.getParameter("action");
            if (idParam == null || !"read".equals(action)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("id and action=read required")));
                return;
            }
            boolean ok = notificationService.markAsRead(Integer.parseInt(idParam));
            resp.getWriter().write(JsonHelper.toJson(ok ? ApiResponse.ok(null) : ApiResponse.fail("Failed")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }
}
