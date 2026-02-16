package com.oceanview.resort.controller;

import com.oceanview.resort.domain.User;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.dto.UserDTO;
import com.oceanview.resort.mapper.UserMapper;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        User user = SessionManager.getInstance().getCurrentUser(req);
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Not authenticated")));
            return;
        }
        UserDTO dto = UserMapper.toDTO(user);
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(dto)));
    }
}
