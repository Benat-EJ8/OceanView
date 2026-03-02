package com.oceanview.resort.controller;

import com.oceanview.resort.domain.Room;
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
import java.util.Optional;

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            Room body = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), Room.class);
            if (body == null || body.getRoomNumber() == null || body.getCategoryId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("roomNumber and categoryId required")));
                return;
            }
            int branchId = body.getBranchId() != null ? body.getBranchId() : 1;
            int floor = body.getFloor() != null ? body.getFloor() : 1;
            Optional<Room> created = roomService.create(body.getRoomNumber(), floor, body.getCategoryId(), branchId);
            if (created.isPresent()) {
                if (body.getViewType() != null) {
                    Room r = created.get();
                    r.setViewType(body.getViewType());
                    roomService.update(r);
                }
                resp.getWriter().write(
                        JsonHelper.toJson(ApiResponse.ok(com.oceanview.resort.mapper.RoomMapper.toDTO(created.get()))));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Failed to create room")));
            }
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
            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("id required")));
                return;
            }
            int id = Integer.parseInt(idParam);
            Room body = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), Room.class);
            if (body == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Request body required")));
                return;
            }
            body.setId(id);
            boolean ok = roomService.update(body);
            resp.getWriter().write(JsonHelper.toJson(ok ? ApiResponse.ok(null) : ApiResponse.fail("Update failed")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String idParam = req.getParameter("id");
            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("id required")));
                return;
            }
            boolean ok = roomService.delete(Integer.parseInt(idParam));
            resp.getWriter().write(JsonHelper.toJson(ok ? ApiResponse.ok(null) : ApiResponse.fail("Delete failed")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Error: " + e.getMessage())));
        }
    }
}
