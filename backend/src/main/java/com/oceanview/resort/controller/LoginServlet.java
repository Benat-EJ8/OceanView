package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.dto.LoginRequest;
import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.mapper.UserMapper;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.service.AuthService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class LoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        LoginRequest body = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), LoginRequest.class);
        if (body == null || body.getUsername() == null || body.getPassword() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Username and password required")));
            return;
        }
        Optional<com.oceanview.resort.domain.User> user = authService.login(body.getUsername(), body.getPassword(), req);
        if (user.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Invalid credentials or account locked")));
            return;
        }
        SessionManager.getInstance().createSession(req, user.get());
        UserDTO dto = UserMapper.toDTO(user.get());
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(dto)));
    }
}
