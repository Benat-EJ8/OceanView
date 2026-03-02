package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.service.ReportService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class ReportsServlet extends HttpServlet {
    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String type = req.getParameter("type");
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        Integer branchId = 1;
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusMonths(1);
        LocalDate toDate = to != null ? LocalDate.parse(to) : LocalDate.now();
        Map<String, Object> data;
        if ("occupancy".equals(type))
            data = reportService.getOccupancyReport(branchId, fromDate, toDate);
        else if ("revenue".equals(type))
            data = reportService.getRevenueReport(branchId, fromDate, toDate);
        else if ("bookings".equals(type))
            data = reportService.getBookingStats(branchId, fromDate, toDate);
        else if ("staff".equals(type))
            data = reportService.getStaffPerformance(branchId, fromDate, toDate);
        else if ("services".equals(type))
            data = reportService.getServiceStats(branchId, fromDate, toDate);
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter()
                    .write(JsonHelper.toJson(ApiResponse.fail("type=occupancy|revenue|bookings|staff|services")));
            return;
        }
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(data)));
    }
}
