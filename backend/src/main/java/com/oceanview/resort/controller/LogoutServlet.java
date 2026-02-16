package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionManager.getInstance().invalidate(req);
        resp.setContentType("application/json");
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(null)));
    }
}
