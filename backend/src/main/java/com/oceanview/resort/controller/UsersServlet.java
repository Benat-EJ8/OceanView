package com.oceanview.resort.controller;

import com.oceanview.resort.domain.User;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.mapper.UserMapper;
import com.oceanview.resort.service.UserService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UsersServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String role = req.getParameter("role");
        List<UserDTO> list = role != null ? userService.findByRole(role) : userService.findAll();
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        User user = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), User.class);
        String password = req.getParameter("password");
        if (user == null || password == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("User data and password required")));
            return;
        }
        Optional<User> created = userService.create(user, password, req);
        if (created.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Username or email already exists")));
            return;
        }
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(UserMapper.toDTO(created.get()))));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("id required")));
            return;
        }
        User user = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), User.class);
        if (user == null || !idParam.equals(String.valueOf(user.getId()))) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Invalid user data")));
            return;
        }
        boolean ok = userService.update(user, req);
        if (!ok) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Update failed")));
            return;
        }
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(userService.findById(user.getId()).orElse(null))));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("id required")));
            return;
        }
        boolean ok = userService.deleteById(Integer.parseInt(idParam));
        resp.getWriter().write(JsonHelper.toJson(ok ? ApiResponse.ok(null) : ApiResponse.fail("Delete failed")));
    }
}
