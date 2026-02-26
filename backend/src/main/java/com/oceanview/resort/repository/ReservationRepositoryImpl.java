package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Reservation;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepositoryImpl implements ReservationRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public Optional<Reservation> findById(Integer id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
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
    public List<Reservation> findByGuestId(Integer guestId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE guest_id = ? ORDER BY check_in_date DESC";
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
    public List<Reservation> findByBranchId(Integer branchId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE branch_id = ? ORDER BY check_in_date DESC";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
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
    public List<Reservation> findByRoomIdAndOverlappingDates(Integer roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE room_id = ? AND status NOT IN ('CANCELLED','NO_SHOW') AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(checkOut));
            ps.setDate(3, Date.valueOf(checkIn));
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
    public List<Reservation> findByStatus(String status) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY check_in_date";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
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
    public boolean save(Reservation r) {
        String sql = "INSERT INTO reservations (branch_id, guest_id, room_id, status, check_in_date, check_out_date, adults, children, special_requests, deposit_amount, created_by) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getBranchId());
            ps.setInt(2, r.getGuestId());
            ps.setObject(3, r.getRoomId());
            ps.setString(4, r.getStatus() != null ? r.getStatus() : "PENDING_APPROVAL");
            ps.setDate(5, Date.valueOf(r.getCheckInDate()));
            ps.setDate(6, Date.valueOf(r.getCheckOutDate()));
            ps.setInt(7, r.getAdults() != null ? r.getAdults() : 1);
            ps.setInt(8, r.getChildren() != null ? r.getChildren() : 0);
            ps.setString(9, r.getSpecialRequests());
            ps.setBigDecimal(10, r.getDepositAmount());
            ps.setObject(11, r.getCreatedBy());
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
    public boolean update(Reservation r) {
        String sql = "UPDATE reservations SET room_id=?, status=?, check_in_date=?, check_out_date=?, adults=?, children=?, special_requests=?, deposit_amount=?, approved_by=?, approved_at=?, cancelled_at=?, cancel_reason=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, r.getRoomId());
            ps.setString(2, r.getStatus());
            ps.setDate(3, Date.valueOf(r.getCheckInDate()));
            ps.setDate(4, Date.valueOf(r.getCheckOutDate()));
            ps.setInt(5, r.getAdults());
            ps.setInt(6, r.getChildren());
            ps.setString(7, r.getSpecialRequests());
            ps.setBigDecimal(8, r.getDepositAmount());
            ps.setObject(9, r.getApprovedBy());
            ps.setTimestamp(10, r.getApprovedAt() != null ? Timestamp.from(r.getApprovedAt()) : null);
            ps.setTimestamp(11, r.getCancelledAt() != null ? Timestamp.from(r.getCancelledAt()) : null);
            ps.setString(12, r.getCancelReason());
            ps.setInt(13, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setBranchId(rs.getInt("branch_id"));
        r.setGuestId(rs.getInt("guest_id"));
        r.setRoomId(rs.getObject("room_id", Integer.class));
        r.setStatus(rs.getString("status"));
        r.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        r.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        r.setAdults(rs.getInt("adults"));
        r.setChildren(rs.getInt("children"));
        r.setSpecialRequests(rs.getString("special_requests"));
        r.setDepositAmount(rs.getBigDecimal("deposit_amount"));
        Timestamp t = rs.getTimestamp("deposit_paid_at");
        r.setDepositPaidAt(t != null ? t.toInstant() : null);
        r.setApprovedBy(rs.getObject("approved_by", Integer.class));
        t = rs.getTimestamp("approved_at");
        r.setApprovedAt(t != null ? t.toInstant() : null);
        t = rs.getTimestamp("cancelled_at");
        r.setCancelledAt(t != null ? t.toInstant() : null);
        r.setCancelReason(rs.getString("cancel_reason"));
        r.setCreatedBy(rs.getObject("created_by", Integer.class));
        t = rs.getTimestamp("created_at");
        r.setCreatedAt(t != null ? t.toInstant() : null);
        return r;
    }
}
