package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Role;
import com.oceanview.resort.domain.User;
import com.oceanview.resort.util.DataSourceProvider;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();
    private final RoleRepository roleRepo = new RoleRepositoryImpl();

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
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
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = true";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
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
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users WHERE is_active = true ORDER BY id")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<User> findByRole(String roleCode) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN roles r ON u.role_id = r.id WHERE r.code = ? AND u.is_active = true";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<User> findByBranchId(Integer branchId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE branch_id = ? AND is_active = true";
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
    public boolean save(User user) {
        String sql = "INSERT INTO users (role_id, branch_id, username, email, password_hash, first_name, last_name, phone, is_locked, failed_attempts, is_active) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, user.getRoleId());
            ps.setObject(2, user.getBranchId());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPasswordHash());
            ps.setString(6, user.getFirstName());
            ps.setString(7, user.getLastName());
            ps.setString(8, user.getPhone());
            ps.setBoolean(9, user.getLocked() != null && user.getLocked());
            ps.setInt(10, user.getFailedAttempts() != null ? user.getFailedAttempts() : 0);
            ps.setBoolean(11, user.getActive() == null || user.getActive());
            int rows = ps.executeUpdate();
            if (rows > 0 && ps.getGeneratedKeys().next()) {
                user.setId(ps.getGeneratedKeys().getInt(1));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET role_id=?, branch_id=?, username=?, email=?, first_name=?, last_name=?, phone=?, is_locked=?, locked_until=?, lock_reason=?, failed_attempts=?, last_login_at=?, is_active=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getRoleId());
            ps.setObject(2, user.getBranchId());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getFirstName());
            ps.setString(6, user.getLastName());
            ps.setString(7, user.getPhone());
            ps.setBoolean(8, user.getLocked() != null && user.getLocked());
            ps.setTimestamp(9, user.getLockedUntil() != null ? Timestamp.from(user.getLockedUntil()) : null);
            ps.setString(10, user.getLockReason());
            ps.setInt(11, user.getFailedAttempts() != null ? user.getFailedAttempts() : 0);
            ps.setTimestamp(12, user.getLastLoginAt() != null ? Timestamp.from(user.getLastLoginAt()) : null);
            ps.setBoolean(13, user.getActive() == null || user.getActive());
            ps.setInt(14, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "UPDATE users SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setRoleId(rs.getInt("role_id"));
        u.setBranchId(rs.getObject("branch_id", Integer.class));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setPhone(rs.getString("phone"));
        u.setLocked(rs.getBoolean("is_locked"));
        Timestamp t = rs.getTimestamp("locked_until");
        u.setLockedUntil(t != null ? t.toInstant() : null);
        u.setLockReason(rs.getString("lock_reason"));
        u.setFailedAttempts(rs.getInt("failed_attempts"));
        t = rs.getTimestamp("last_login_at");
        u.setLastLoginAt(t != null ? t.toInstant() : null);
        u.setActive(rs.getBoolean("is_active"));
        t = rs.getTimestamp("created_at");
        u.setCreatedAt(t != null ? t.toInstant() : null);
        roleRepo.findById(u.getRoleId()).ifPresent(u::setRole);
        return u;
    }
}
