package com.oceanview.resort.controller;

import com.oceanview.resort.domain.Promotion;
import com.oceanview.resort.domain.User;
import com.oceanview.resort.dto.ApiResponse;
import com.oceanview.resort.security.SessionManager;
import com.oceanview.resort.service.PromotionService;
import com.oceanview.resort.util.JsonHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class PromotionsServlet extends HttpServlet {
    private final PromotionService promotionService = new PromotionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        List<Promotion> active = promotionService.getActivePromotions();
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(active)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        User current = SessionManager.getInstance().getCurrentUser(req);
        String roleCode = current != null && current.getRole() != null ? current.getRole().getCode() : null;
        if (current == null || (roleCode != null && !("MANAGER".equals(roleCode) || "ADMIN".equals(roleCode)))) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Only managers or admins can manage promotions")));
            return;
        }

        Promotion body = JsonHelper.fromJson(req.getReader().lines().reduce("", (a, b) -> a + b), Promotion.class);
        if (body == null || body.getCode() == null || body.getName() == null || body.getDiscountType() == null
                || body.getDiscountValue() == null || body.getValidFrom() == null || body.getValidTo() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail(
                    "code, name, discountType, discountValue, validFrom, validTo are required")));
            return;
        }
        if (body.getActive() == null) {
            body.setActive(true);
        }

        Promotion created = promotionService.create(body);
        if (created == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonHelper.toJson(ApiResponse.fail("Failed to create promotion")));
            return;
        }
        resp.getWriter().write(JsonHelper.toJson(ApiResponse.ok(created)));
    }
}

