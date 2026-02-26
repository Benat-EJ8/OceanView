package com.oceanview.resort.controller;

import com.oceanview.resort.domain.ServiceRequest;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.service.ServiceRequestService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ServiceRequestServlet extends HttpServlet {
    private final ServiceRequestService serviceRequestService = new ServiceRequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String guestId = req.getParameter("guestId");
            List<ServiceRequest> list;
            if (guestId != null) {
                list = serviceRequestService.findByGuestId(Integer.parseInt(guestId));
            } else {
                list = serviceRequestService.findAll();
            }
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(list)));
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
            ServiceRequest body = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b),
                    ServiceRequest.class);
            if (body == null || body.getRequestType() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("requestType required")));
                return;
            }
            Integer userId = SessionManager.getInstance().getCurrentUserId(req);
            body.setGuestId(userId);
            serviceRequestService.create(body)
                    .ifPresentOrElse(
                            created -> {
                                try {
                                    resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(created)));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            () -> {
                                try {
                                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                    resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Failed to create")));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String idParam = req.getParameter("id");
            String status = req.getParameter("status");
            if (idParam == null || status == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("id and status required")));
                return;
            }
            boolean ok = serviceRequestService.updateStatus(Integer.parseInt(idParam), status);
            resp.getWriter().write(JsonHelper.toJson(ok ? ApiResponse.ok(null) : ApiResponse.fail("Update failed")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }
}
