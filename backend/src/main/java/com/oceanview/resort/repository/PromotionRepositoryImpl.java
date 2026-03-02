package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Promotion;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionRepositoryImpl implements PromotionRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public List<Promotion> findActivePromotions() {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT * FROM promotions " +
                "WHERE is_active = true " +
                "AND valid_from <= CURRENT_DATE " +
                "AND valid_to >= CURRENT_DATE " +
                "AND (max_uses IS NULL OR used_count < max_uses) " +
                "ORDER BY valid_from, created_at";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public boolean save(Promotion p) {
        String sql = "INSERT INTO promotions (code, name, description, discount_type, discount_value, min_stay_nights, valid_from, valid_to, max_uses, used_count, is_active) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCode());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getDiscountType());
            ps.setBigDecimal(5, p.getDiscountValue());
            ps.setObject(6, p.getMinStayNights());
            ps.setDate(7, p.getValidFrom() != null ? Date.valueOf(p.getValidFrom()) : null);
            ps.setDate(8, p.getValidTo() != null ? Date.valueOf(p.getValidTo()) : null);
            ps.setObject(9, p.getMaxUses());
            ps.setObject(10, p.getUsedCount() != null ? p.getUsedCount() : 0);
            ps.setBoolean(11, p.getActive() == null || p.getActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Promotion mapRow(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setId(rs.getInt("id"));
        p.setCode(rs.getString("code"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setDiscountType(rs.getString("discount_type"));
        p.setDiscountValue(rs.getBigDecimal("discount_value"));
        p.setMinStayNights((Integer) rs.getObject("min_stay_nights"));
        Date vf = rs.getDate("valid_from");
        Date vt = rs.getDate("valid_to");
        p.setValidFrom(vf != null ? vf.toLocalDate() : null);
        p.setValidTo(vt != null ? vt.toLocalDate() : null);
        p.setMaxUses((Integer) rs.getObject("max_uses"));
        p.setUsedCount((Integer) rs.getObject("used_count"));
        p.setActive(rs.getBoolean("is_active"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        p.setCreatedAt(createdAt != null ? createdAt.toInstant() : Instant.now());
        return p;
    }
}

