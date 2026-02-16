package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Guest;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuestRepositoryImpl implements GuestRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public Optional<Guest> findById(Integer id) {
        String sql = "SELECT * FROM guests WHERE id = ?";
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
    public Optional<Guest> findByUserId(Integer userId) {
        String sql = "SELECT * FROM guests WHERE user_id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Guest> findByEmail(String email) {
        String sql = "SELECT * FROM guests WHERE email = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Guest> findAll() {
        List<Guest> list = new ArrayList<>();
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM guests ORDER BY id")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public boolean save(Guest g) {
        String sql = "INSERT INTO guests (user_id, first_name, last_name, email, phone, id_type, id_number, nationality, date_of_birth, loyalty_points, is_blacklisted, notes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, g.getUserId());
            ps.setString(2, g.getFirstName());
            ps.setString(3, g.getLastName());
            ps.setString(4, g.getEmail());
            ps.setString(5, g.getPhone());
            ps.setString(6, g.getIdType());
            ps.setString(7, g.getIdNumber());
            ps.setString(8, g.getNationality());
            ps.setObject(9, g.getDateOfBirth() != null ? Date.valueOf(g.getDateOfBirth()) : null);
            ps.setInt(10, g.getLoyaltyPoints() != null ? g.getLoyaltyPoints() : 0);
            ps.setBoolean(11, g.getBlacklisted() != null && g.getBlacklisted());
            ps.setString(12, g.getNotes());
            int rows = ps.executeUpdate();
            if (rows > 0 && ps.getGeneratedKeys().next()) {
                g.setId(ps.getGeneratedKeys().getInt(1));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean update(Guest g) {
        String sql = "UPDATE guests SET first_name=?, last_name=?, email=?, phone=?, id_type=?, id_number=?, nationality=?, date_of_birth=?, loyalty_points=?, is_blacklisted=?, blacklist_reason=?, notes=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getFirstName());
            ps.setString(2, g.getLastName());
            ps.setString(3, g.getEmail());
            ps.setString(4, g.getPhone());
            ps.setString(5, g.getIdType());
            ps.setString(6, g.getIdNumber());
            ps.setString(7, g.getNationality());
            ps.setObject(8, g.getDateOfBirth() != null ? Date.valueOf(g.getDateOfBirth()) : null);
            ps.setInt(9, g.getLoyaltyPoints() != null ? g.getLoyaltyPoints() : 0);
            ps.setBoolean(10, g.getBlacklisted() != null && g.getBlacklisted());
            ps.setString(11, g.getBlacklistReason());
            ps.setString(12, g.getNotes());
            ps.setInt(13, g.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setId(rs.getInt("id"));
        g.setUserId(rs.getObject("user_id", Integer.class));
        g.setFirstName(rs.getString("first_name"));
        g.setLastName(rs.getString("last_name"));
        g.setEmail(rs.getString("email"));
        g.setPhone(rs.getString("phone"));
        g.setIdType(rs.getString("id_type"));
        g.setIdNumber(rs.getString("id_number"));
        g.setNationality(rs.getString("nationality"));
        Date d = rs.getDate("date_of_birth");
        g.setDateOfBirth(d != null ? d.toLocalDate() : null);
        g.setLoyaltyPoints(rs.getInt("loyalty_points"));
        g.setBlacklisted(rs.getBoolean("is_blacklisted"));
        g.setBlacklistReason(rs.getString("blacklist_reason"));
        g.setNotes(rs.getString("notes"));
        Timestamp t = rs.getTimestamp("created_at");
        g.setCreatedAt(t != null ? t.toInstant() : null);
        return g;
    }
}
