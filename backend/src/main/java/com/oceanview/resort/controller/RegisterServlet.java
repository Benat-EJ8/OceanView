package com.oceanview.resort.controller;

import com.oceanview.resort.domain.User;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.dto.RegisterRequest;
import com.oceanview.resort.repository.RoleRepositoryImpl;
import com.oceanview.resort.service.UserService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserService();
    private final RoleRepositoryImpl roleRepo = new RoleRepositoryImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        RegisterRequest body = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), RegisterRequest.class);
        if (body == null || body.getUsername() == null || body.getPassword() == null || body.getEmail() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("username, password, email, firstName, lastName required")));
            return;
        }
        User user = new User();
        user.setUsername(body.getUsername());
        user.setEmail(body.getEmail());
        user.setFirstName(body.getFirstName() != null ? body.getFirstName() : "");
        user.setLastName(body.getLastName() != null ? body.getLastName() : "");
        user.setPasswordHash("");
        roleRepo.findByCode("CUSTOMER").ifPresent(r -> user.setRoleId(r.getId()));
        user.setBranchId(1);
        Optional<User> created = userService.create(user, body.getPassword(), req);
        if (created.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Username or email already exists")));
            return;
        }
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(com.oceanview.resort.mapper.UserMapper.toDTO(created.get()))));
    }
}
