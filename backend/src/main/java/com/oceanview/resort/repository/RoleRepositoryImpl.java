package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Role;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.util.Optional;

public class RoleRepositoryImpl implements RoleRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public Optional<Role> findById(Integer id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByCode(String code) {
        String sql = "SELECT * FROM roles WHERE code = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private Role mapRow(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setId(rs.getInt("id"));
        r.setCode(rs.getString("code"));
        r.setName(rs.getString("name"));
        r.setDescription(rs.getString("description"));
        Timestamp t = rs.getTimestamp("created_at");
        r.setCreatedAt(t != null ? t.toInstant() : null);
        return r;
    }
}
