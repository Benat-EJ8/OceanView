package com.oceanview.resort.controller;

import com.oceanview.resort.domain.RoomCategory;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.service.RoomService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class RoomCategoriesServlet extends HttpServlet {
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            List<RoomCategory> list = roomService.findAllCategories();
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter()
                    .write(JsonHelper.toJson(ApiResponse.fail("Failed to load room categories: " + e.getMessage())));
        }
    }
}
