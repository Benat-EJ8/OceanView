package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Branch;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BranchRepositoryImpl implements BranchRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public Optional<Branch> findById(Integer id) {
        String sql = "SELECT * FROM branches WHERE id = ?";
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
    public List<Branch> findAll() {
        List<Branch> list = new ArrayList<>();
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM branches WHERE is_active = true ORDER BY id")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Branch> findByParentId(Integer parentId) {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE parent_id = ? AND is_active = true";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, parentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private Branch mapRow(ResultSet rs) throws SQLException {
        Branch b = new Branch();
        b.setId(rs.getInt("id"));
        b.setParentId(rs.getObject("parent_id", Integer.class));
        b.setName(rs.getString("name"));
        b.setCode(rs.getString("code"));
        b.setAddress(rs.getString("address"));
        b.setCity(rs.getString("city"));
        b.setCountry(rs.getString("country"));
        b.setPhone(rs.getString("phone"));
        b.setEmail(rs.getString("email"));
        b.setActive(rs.getBoolean("is_active"));
        Timestamp t = rs.getTimestamp("created_at");
        b.setCreatedAt(t != null ? t.toInstant() : null);
        return b;
    }
}
