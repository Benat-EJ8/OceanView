package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.service.RoomService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class RoomsServlet extends HttpServlet {
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String available = req.getParameter("available");
            String branchIdParam = req.getParameter("branchId");
            int branchId = branchIdParam != null ? Integer.parseInt(branchIdParam) : 1;
            if ("true".equals(available)) {
                String checkIn = req.getParameter("checkIn");
                String checkOut = req.getParameter("checkOut");
                LocalDate ci = checkIn != null ? LocalDate.parse(checkIn) : LocalDate.now();
                LocalDate co = checkOut != null ? LocalDate.parse(checkOut) : LocalDate.now().plusDays(1);
                List<RoomDTO> list = roomService.findAvailable(branchId, ci, co);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
            } else {
                List<RoomDTO> list = roomService.findByBranchId(branchId);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Failed to load rooms: " + e.getMessage())));
        }
    }
}
