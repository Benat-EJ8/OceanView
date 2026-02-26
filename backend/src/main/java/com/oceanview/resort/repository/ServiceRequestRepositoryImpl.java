package com.oceanview.resort.repository;

import com.oceanview.resort.domain.ServiceRequest;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceRequestRepositoryImpl implements ServiceRequestRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public List<ServiceRequest> findAll() {
        List<ServiceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM service_requests ORDER BY created_at DESC";
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
    public List<ServiceRequest> findByGuestId(Integer guestId) {
        List<ServiceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM service_requests WHERE guest_id = ? ORDER BY created_at DESC";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
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
    public Optional<ServiceRequest> findById(Integer id) {
        String sql = "SELECT * FROM service_requests WHERE id = ?";
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
    public boolean save(ServiceRequest r) {
        String sql = "INSERT INTO service_requests (reservation_id, guest_id, request_type, description, status) VALUES (?,?,?,?,?)";
        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, r.getReservationId());
            ps.setObject(2, r.getGuestId());
            ps.setString(3, r.getRequestType());
            ps.setString(4, r.getDescription());
            ps.setString(5, "PENDING");
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
        String sql = "UPDATE service_requests SET status=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ServiceRequest mapRow(ResultSet rs) throws SQLException {
        ServiceRequest r = new ServiceRequest();
        r.setId(rs.getInt("id"));
        r.setReservationId(rs.getObject("reservation_id", Integer.class));
        r.setGuestId(rs.getObject("guest_id", Integer.class));
        r.setRequestType(rs.getString("request_type"));
        r.setDescription(rs.getString("description"));
        r.setStatus(rs.getString("status"));
        Timestamp t = rs.getTimestamp("created_at");
        r.setCreatedAt(t != null ? t.toInstant() : null);
        t = rs.getTimestamp("updated_at");
        r.setUpdatedAt(t != null ? t.toInstant() : null);
        return r;
    }
}
