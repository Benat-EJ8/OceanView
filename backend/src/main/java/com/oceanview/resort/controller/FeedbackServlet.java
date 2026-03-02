package com.oceanview.resort.controller;

import com.oceanview.resort.domain.Feedback;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.service.FeedbackService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class FeedbackServlet extends HttpServlet {
    private final FeedbackService feedbackService = new FeedbackService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String guestId = req.getParameter("guestId");
            List<Feedback> list;
            if (guestId != null) {
                list = feedbackService.findByGuestId(Integer.parseInt(guestId));
            } else {
                list = feedbackService.findAll();
            }
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            Feedback body = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), Feedback.class);
            if (body == null || body.getRating() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("rating required")));
                return;
            }
            boolean ok = feedbackService.create(body);
            resp.getWriter().write(
                    JsonHelper.toJson(ok ? ApiResponse.ok(body) : ApiResponse.fail("Failed to submit feedback")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }
}
