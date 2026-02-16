package com.oceanview.resort.repository;

import com.oceanview.resort.domain.ActivityLog;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogRepositoryImpl implements ActivityLogRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public void save(ActivityLog log) {
        String sql = "INSERT INTO activity_logs (user_id, action, entity_type, entity_id, details, ip_address) VALUES (?,?,?,?,?::jsonb,?)";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, log.getUserId());
            ps.setString(2, log.getAction());
            ps.setString(3, log.getEntityType());
            ps.setString(4, log.getEntityId());
            ps.setString(5, log.getDetailsJson());
            ps.setString(6, log.getIpAddress());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActivityLog> findByUserId(Integer userId, int limit) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private ActivityLog mapRow(ResultSet rs) throws SQLException {
        ActivityLog a = new ActivityLog();
        a.setId(rs.getInt("id"));
        a.setUserId(rs.getObject("user_id", Integer.class));
        a.setAction(rs.getString("action"));
        a.setEntityType(rs.getString("entity_type"));
        a.setEntityId(rs.getString("entity_id"));
        a.setDetailsJson(rs.getString("details"));
        a.setIpAddress(rs.getString("ip_address"));
        Timestamp t = rs.getTimestamp("created_at");
        a.setCreatedAt(t != null ? t.toInstant() : null);
        return a;
    }
}
