package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Notification;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public List<Notification> findByUserId(Integer userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public boolean save(Notification n) {
        String sql = "INSERT INTO notifications (user_id, guest_id, type, title, body, entity_type, entity_id) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, n.getUserId());
            ps.setObject(2, n.getGuestId());
            ps.setString(3, n.getType());
            ps.setString(4, n.getTitle());
            ps.setString(5, n.getBody());
            ps.setString(6, n.getEntityType());
            ps.setString(7, n.getEntityId());
            int rows = ps.executeUpdate();
            if (rows > 0 && ps.getGeneratedKeys().next()) {
                n.setId(ps.getGeneratedKeys().getInt(1));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean markAsRead(Integer id) {
        String sql = "UPDATE notifications SET is_read=true, read_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countUnreadByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getObject("user_id", Integer.class));
        n.setGuestId(rs.getObject("guest_id", Integer.class));
        n.setType(rs.getString("type"));
        n.setTitle(rs.getString("title"));
        n.setBody(rs.getString("body"));
        n.setEntityType(rs.getString("entity_type"));
        n.setEntityId(rs.getString("entity_id"));
        n.setRead(rs.getBoolean("is_read"));
        Timestamp t = rs.getTimestamp("read_at");
        n.setReadAt(t != null ? t.toInstant() : null);
        t = rs.getTimestamp("created_at");
        n.setCreatedAt(t != null ? t.toInstant() : null);
        return n;
    }
}
