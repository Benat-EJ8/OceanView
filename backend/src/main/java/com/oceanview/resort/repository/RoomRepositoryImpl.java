package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Room;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepositoryImpl implements RoomRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();
    private final RoomCategoryRepository categoryRepo = new RoomCategoryRepositoryImpl();

    @Override
    public Optional<Room> findById(Integer id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
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
    public List<Room> findByBranchId(Integer branchId) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE branch_id = ? ORDER BY floor, room_number";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Room> findByCategoryId(Integer categoryId) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE category_id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Room> findAvailableByBranchAndDates(Integer branchId, LocalDate checkIn, LocalDate checkOut) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.* FROM rooms r WHERE r.branch_id = ? AND r.status = 'AVAILABLE' AND NOT EXISTS (SELECT 1 FROM reservations res WHERE res.room_id = r.id AND res.status NOT IN ('CANCELLED','NO_SHOW') AND res.check_in_date < ? AND res.check_out_date > ?) ORDER BY r.floor, r.room_number";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, Date.valueOf(checkOut));
            ps.setDate(3, Date.valueOf(checkIn));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public boolean save(Room room) {
        String sql = "INSERT INTO rooms (branch_id, category_id, room_number, floor, status, view_type) VALUES (?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, room.getBranchId());
            ps.setInt(2, room.getCategoryId());
            ps.setString(3, room.getRoomNumber());
            ps.setInt(4, room.getFloor());
            ps.setString(5, room.getStatus() != null ? room.getStatus() : "AVAILABLE");
            ps.setString(6, room.getViewType());
            int rows = ps.executeUpdate();
            if (rows > 0 && ps.getGeneratedKeys().next()) {
                room.setId(ps.getGeneratedKeys().getInt(1));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean update(Room room) {
        String sql = "UPDATE rooms SET category_id=?, room_number=?, floor=?, status=?, view_type=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, room.getCategoryId());
            ps.setString(2, room.getRoomNumber());
            ps.setInt(3, room.getFloor());
            ps.setString(4, room.getStatus());
            ps.setString(5, room.getViewType());
            ps.setInt(6, room.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt("id"));
        r.setBranchId(rs.getInt("branch_id"));
        r.setCategoryId(rs.getInt("category_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setFloor(rs.getInt("floor"));
        r.setStatus(rs.getString("status"));
        r.setViewType(rs.getString("view_type"));
        categoryRepo.findById(r.getCategoryId()).ifPresent(r::setCategory);
        return r;
    }
}
