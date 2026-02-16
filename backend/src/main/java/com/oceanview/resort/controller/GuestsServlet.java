package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.repository.GuestRepositoryImpl;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GuestsServlet extends HttpServlet {
    private final GuestRepositoryImpl guestRepo = new GuestRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Integer userId = SessionManager.getInstance().getCurrentUserId(req);
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Not authenticated")));
            return;
        }
        var opt = guestRepo.findByUserId(userId);
        if (opt.isPresent()) {
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(opt.get())));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("No guest profile")));
        }
    }
}
