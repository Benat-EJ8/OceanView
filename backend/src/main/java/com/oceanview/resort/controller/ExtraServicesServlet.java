package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.service.ExtraServicesService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExtraServicesServlet extends HttpServlet {
    private final ExtraServicesService extraServicesService = new ExtraServicesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String reservationId = req.getParameter("reservationId");
            if (reservationId != null) {
                List<Map<String, Object>> list = extraServicesService.getByReservation(Integer.parseInt(reservationId));
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
            } else {
                List<Map<String, Object>> list = extraServicesService.findAllActive();
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
            }
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
            String body = req.getReader().lines().reduce("", (a, b) -> a + b);
            Map<String, Object> data = JsonHelper.fromJson(body, Map.class);
            if (data == null || data.get("reservationId") == null || data.get("extraId") == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("reservationId and extraId required")));
                return;
            }
            int reservationId = ((Number) data.get("reservationId")).intValue();
            int extraId = ((Number) data.get("extraId")).intValue();
            int quantity = data.get("quantity") != null ? ((Number) data.get("quantity")).intValue() : 1;
            boolean ok = extraServicesService.addToReservation(reservationId, extraId, quantity);
            resp.getWriter()
                    .write(JsonHelper.toJson(ok ? ApiResponse.ok(null) : ApiResponse.fail("Failed to add service")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }
}
