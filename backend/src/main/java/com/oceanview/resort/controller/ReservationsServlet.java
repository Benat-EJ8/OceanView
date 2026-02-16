package com.oceanview.resort.controller;

import com.oceanview.resort.domain.Reservation;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.service.ReservationService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ReservationsServlet extends HttpServlet {
    private final ReservationService reservationService = new ReservationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String idParam = req.getParameter("id");
        String guestIdParam = req.getParameter("guestId");
        String branchIdParam = req.getParameter("branchId");
        String pending = req.getParameter("pending");
        if (idParam != null) {
            Optional<ReservationDTO> opt = reservationService.findById(Integer.parseInt(idParam));
            if (opt.isEmpty()) resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            else resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(opt.get())));
            return;
        }
        if ("true".equals(pending)) {
            List<ReservationDTO> list = reservationService.findPendingApproval();
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
            return;
        }
        if (guestIdParam != null) {
            List<ReservationDTO> list = reservationService.findByGuestId(Integer.parseInt(guestIdParam));
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
            return;
        }
        int branchId = branchIdParam != null ? Integer.parseInt(branchIdParam) : 1;
        List<ReservationDTO> list = reservationService.findByBranchId(branchId);
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Reservation body = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), Reservation.class);
        if (body == null || body.getGuestId() == null || body.getCheckInDate() == null || body.getCheckOutDate() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Guest, check-in and check-out required")));
            return;
        }
        Integer createdBy = SessionManager.getInstance().getCurrentUserId(req);
        body.setBranchId(body.getBranchId() != null ? body.getBranchId() : 1);
        Optional<Reservation> created = reservationService.create(body, createdBy, req);
        if (created.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Validation failed or room not available")));
            return;
        }
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(com.oceanview.resort.mapper.ReservationMapper.toDTO(created.get()))));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String action = req.getParameter("action");
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("id required")));
            return;
        }
        int id = Integer.parseInt(idParam);
        Integer userId = SessionManager.getInstance().getCurrentUserId(req);
        if ("approve".equals(action)) {
            boolean ok = reservationService.approve(id, userId);
            resp.getWriter().write(JsonHelper.toJson(ok ? ApiResponse.ok(null) : ApiResponse.fail("Cannot approve")));
            return;
        }
        if ("cancel".equals(action)) {
            String reason = req.getParameter("reason");
            boolean ok = reservationService.cancel(id, reason != null ? reason : "Cancelled");
            resp.getWriter().write(JsonHelper.toJson(ok ? ApiResponse.ok(null) : ApiResponse.fail("Cannot cancel")));
            return;
        }
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("action=approve|cancel")));
    }
}
