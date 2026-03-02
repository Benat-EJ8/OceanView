package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Feedback;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackRepositoryImpl implements FeedbackRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public List<Feedback> findAll() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback ORDER BY created_at DESC";
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
    public List<Feedback> findByGuestId(Integer guestId) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE guest_id = ? ORDER BY created_at DESC";
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
    public boolean save(Feedback f) {
        String sql = "INSERT INTO feedback (guest_id, reservation_id, rating, comment, category, is_public) VALUES (?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, f.getGuestId());
            ps.setObject(2, f.getReservationId());
            ps.setInt(3, f.getRating());
            ps.setString(4, f.getComment());
            ps.setString(5, f.getCategory());
            ps.setBoolean(6, f.getPublicVisible() != null ? f.getPublicVisible() : true);
            int rows = ps.executeUpdate();
            if (rows > 0 && ps.getGeneratedKeys().next()) {
                f.setId(ps.getGeneratedKeys().getInt(1));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private Feedback mapRow(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setId(rs.getInt("id"));
        f.setGuestId(rs.getObject("guest_id", Integer.class));
        f.setReservationId(rs.getObject("reservation_id", Integer.class));
        f.setRating(rs.getInt("rating"));
        f.setComment(rs.getString("comment"));
        f.setCategory(rs.getString("category"));
        f.setPublicVisible(rs.getBoolean("is_public"));
        Timestamp t = rs.getTimestamp("created_at");
        f.setCreatedAt(t != null ? t.toInstant() : null);
        return f;
    }
}
