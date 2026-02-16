package com.oceanview.resort.repository;

import com.oceanview.resort.domain.RoomCategory;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomCategoryRepositoryImpl implements RoomCategoryRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public Optional<RoomCategory> findById(Integer id) {
        String sql = "SELECT * FROM room_categories WHERE id = ?";
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
    public List<RoomCategory> findAll() {
        List<RoomCategory> list = new ArrayList<>();
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM room_categories ORDER BY id")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private RoomCategory mapRow(ResultSet rs) throws SQLException {
        RoomCategory c = new RoomCategory();
        c.setId(rs.getInt("id"));
        c.setCode(rs.getString("code"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setBasePrice(rs.getBigDecimal("base_price"));
        c.setMaxOccupancy(rs.getInt("max_occupancy"));
        c.setSizeSqm(rs.getBigDecimal("size_sqm"));
        c.setAmenitiesJson(rs.getString("amenities"));
        c.setImageUrl(rs.getString("image_url"));
        Timestamp t = rs.getTimestamp("created_at");
        c.setCreatedAt(t != null ? t.toInstant() : null);
        return c;
    }
}
