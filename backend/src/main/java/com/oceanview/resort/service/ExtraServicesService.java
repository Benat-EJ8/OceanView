package com.oceanview.resort.service;

import com.oceanview.resort.util.DataSourceProvider;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ExtraServicesService {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    public List<Map<String, Object>> findAllActive() {
        String sql = "SELECT * FROM extra_services WHERE is_active = true ORDER BY name";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("code", rs.getString("code"));
                    row.put("name", rs.getString("name"));
                    row.put("price", rs.getBigDecimal("price"));
                    row.put("taxInclusive", rs.getBoolean("tax_inclusive"));
                    list.add(row);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addToReservation(Integer reservationId, Integer extraId, Integer quantity) {
        // Get the service price
        String priceSql = "SELECT price FROM extra_services WHERE id = ? AND is_active = true";
        try (Connection conn = ds.getConnection()) {
            BigDecimal unitPrice;
            try (PreparedStatement ps = conn.prepareStatement(priceSql)) {
                ps.setInt(1, extraId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        return false;
                    unitPrice = rs.getBigDecimal("price");
                }
            }

            BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(quantity != null ? quantity : 1));
            String sql = "INSERT INTO reservation_extras (reservation_id, extra_id, quantity, unit_price, amount) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, reservationId);
                ps.setInt(2, extraId);
                ps.setInt(3, quantity != null ? quantity : 1);
                ps.setBigDecimal(4, unitPrice);
                ps.setBigDecimal(5, amount);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> getByReservation(Integer reservationId) {
        String sql = "SELECT re.*, es.name, es.code FROM reservation_extras re JOIN extra_services es ON re.extra_id = es.id WHERE re.reservation_id = ? ORDER BY re.created_at";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("reservationId", rs.getInt("reservation_id"));
                    row.put("extraId", rs.getInt("extra_id"));
                    row.put("name", rs.getString("name"));
                    row.put("code", rs.getString("code"));
                    row.put("quantity", rs.getInt("quantity"));
                    row.put("unitPrice", rs.getBigDecimal("unit_price"));
                    row.put("amount", rs.getBigDecimal("amount"));
                    list.add(row);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
