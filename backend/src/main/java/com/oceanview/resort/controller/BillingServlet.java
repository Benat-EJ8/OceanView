package com.oceanview.resort.controller;

import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.service.BillingService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class BillingServlet extends HttpServlet {
    private final BillingService billingService = new BillingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String reservationId = req.getParameter("reservationId");
            if (reservationId == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("reservationId required")));
                return;
            }
            Map<String, Object> invoice = billingService.getInvoiceForReservation(Integer.parseInt(reservationId));
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(invoice)));
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
            String reservationId = req.getParameter("reservationId");
            if (reservationId == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("reservationId required")));
                return;
            }
            Map<String, Object> invoice = billingService.generateInvoice(Integer.parseInt(reservationId));
            if (invoice == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(
                        ApiResponse.fail("Could not generate invoice – check reservation has a room assigned")));
                return;
            }
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(invoice)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }
}
