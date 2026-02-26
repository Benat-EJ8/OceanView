package com.oceanview.resort.repository;

import com.oceanview.resort.domain.MaintenanceRequest;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaintenanceRepositoryImpl implements MaintenanceRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public List<MaintenanceRequest> findAll() {
        List<MaintenanceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_requests ORDER BY created_at DESC";
        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<MaintenanceRequest> findByRoomId(Integer roomId) {
        List<MaintenanceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_requests WHERE room_id = ? ORDER BY created_at DESC";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
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
    public List<MaintenanceRequest> findByReportedBy(Integer userId) {
        List<MaintenanceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_requests WHERE reported_by = ? ORDER BY created_at DESC";
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
    public Optional<MaintenanceRequest> findById(Integer id) {
        String sql = "SELECT * FROM maintenance_requests WHERE id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(MaintenanceRequest r) {
        String sql = "INSERT INTO maintenance_requests (room_id, reported_by, title, description, priority, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getRoomId());
            ps.setObject(2, r.getReportedBy());
            ps.setString(3, r.getTitle());
            ps.setString(4, r.getDescription());
            ps.setString(5, r.getPriority() != null ? r.getPriority() : "NORMAL");
            ps.setString(6, "OPEN");
            int rows = ps.executeUpdate();
            if (rows > 0 && ps.getGeneratedKeys().next()) {
                r.setId(ps.getGeneratedKeys().getInt(1));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean updateStatus(Integer id, String status) {
        String sql = "UPDATE maintenance_requests SET status=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private MaintenanceRequest mapRow(ResultSet rs) throws SQLException {
        MaintenanceRequest r = new MaintenanceRequest();
        r.setId(rs.getInt("id"));
        r.setRoomId(rs.getInt("room_id"));
        r.setReportedBy(rs.getObject("reported_by", Integer.class));
        r.setTitle(rs.getString("title"));
        r.setDescription(rs.getString("description"));
        r.setPriority(rs.getString("priority"));
        r.setStatus(rs.getString("status"));
        r.setAssignedTo(rs.getObject("assigned_to", Integer.class));
        Timestamp t = rs.getTimestamp("completed_at");
        r.setCompletedAt(t != null ? t.toInstant() : null);
        t = rs.getTimestamp("created_at");
        r.setCreatedAt(t != null ? t.toInstant() : null);
        t = rs.getTimestamp("updated_at");
        r.setUpdatedAt(t != null ? t.toInstant() : null);
        return r;
    }
}
