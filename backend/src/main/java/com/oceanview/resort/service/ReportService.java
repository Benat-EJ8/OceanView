package com.oceanview.resort.service;

import com.oceanview.resort.util.DataSourceProvider;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService implements com.oceanview.resort.patterns.reports.ReportFacade {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();

    @Override
    public Map<String, Object> getOccupancyReport(Integer branchId, LocalDate from, LocalDate to) {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT COUNT(DISTINCT r.id) AS total_rooms, " +
                "COUNT(DISTINCT CASE WHEN res.id IS NOT NULL AND res.status NOT IN ('CANCELLED','NO_SHOW') THEN r.id END) AS occupied "
                +
                "FROM rooms r LEFT JOIN reservations res ON res.room_id = r.id AND res.check_in_date < ? AND res.check_out_date > ? AND res.status NOT IN ('CANCELLED','NO_SHOW') "
                +
                "WHERE r.branch_id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(to));
            ps.setDate(2, Date.valueOf(from));
            ps.setInt(3, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_rooms");
                    int occupied = rs.getInt("occupied");
                    result.put("totalRooms", total);
                    result.put("occupiedRooms", occupied);
                    result.put("availableRooms", total - occupied);
                    result.put("occupancyRate",
                            total > 0 ? BigDecimal.valueOf(occupied)
                                    .divide(BigDecimal.valueOf(total), 4, java.math.RoundingMode.HALF_UP).doubleValue()
                                    : 0);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Room status breakdown
        String statusSql = "SELECT status, COUNT(*) AS cnt FROM rooms WHERE branch_id = ? GROUP BY status";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(statusSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> statusBreakdown = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("status", rs.getString("status"));
                    row.put("count", rs.getInt("cnt"));
                    statusBreakdown.add(row);
                }
                result.put("statusBreakdown", statusBreakdown);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Category breakdown
        String catSql = "SELECT rc.name, COUNT(r.id) AS total, " +
                "COUNT(CASE WHEN r.status = 'OCCUPIED' THEN 1 END) AS occupied " +
                "FROM rooms r JOIN room_categories rc ON r.category_id = rc.id WHERE r.branch_id = ? GROUP BY rc.name";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(catSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> categoryBreakdown = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("category", rs.getString("name"));
                    row.put("total", rs.getInt("total"));
                    row.put("occupied", rs.getInt("occupied"));
                    categoryBreakdown.add(row);
                }
                result.put("categoryBreakdown", categoryBreakdown);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public Map<String, Object> getRevenueReport(Integer branchId, LocalDate from, LocalDate to) {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT COALESCE(SUM(total_amount),0) AS revenue FROM invoices i JOIN reservations res ON i.reservation_id = res.id WHERE res.branch_id = ? AND i.created_at::date BETWEEN ? AND ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    result.put("revenue", rs.getBigDecimal("revenue"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Revenue by room category
        String catSql = "SELECT rc.name AS category, COALESCE(SUM(ili.amount),0) AS revenue " +
                "FROM invoice_line_items ili JOIN invoices i ON ili.invoice_id = i.id " +
                "JOIN reservations res ON i.reservation_id = res.id " +
                "LEFT JOIN rooms rm ON res.room_id = rm.id LEFT JOIN room_categories rc ON rm.category_id = rc.id " +
                "WHERE res.branch_id = ? AND i.created_at::date BETWEEN ? AND ? AND ili.line_type = 'ROOM' " +
                "GROUP BY rc.name";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(catSql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> byCategory = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("category", rs.getString("category"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    byCategory.add(row);
                }
                result.put("revenueByCategory", byCategory);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Services revenue
        String svcSql = "SELECT COALESCE(SUM(ili.amount),0) AS service_revenue " +
                "FROM invoice_line_items ili JOIN invoices i ON ili.invoice_id = i.id " +
                "JOIN reservations res ON i.reservation_id = res.id " +
                "WHERE res.branch_id = ? AND i.created_at::date BETWEEN ? AND ? AND ili.line_type = 'SERVICE'";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(svcSql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    result.put("serviceRevenue", rs.getBigDecimal("service_revenue"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public Map<String, Object> getBookingStats(Integer branchId, LocalDate from, LocalDate to) {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT status, COUNT(*) AS cnt FROM reservations WHERE branch_id = ? AND check_in_date BETWEEN ? AND ? GROUP BY status";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> byStatus = new ArrayList<>();
                int total = 0;
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("status", rs.getString("status"));
                    int cnt = rs.getInt("cnt");
                    row.put("count", cnt);
                    total += cnt;
                    byStatus.add(row);
                }
                result.put("byStatus", byStatus);
                result.put("totalBookings", total);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Average stay duration
        String avgSql = "SELECT COALESCE(AVG(check_out_date - check_in_date), 0) AS avg_stay FROM reservations WHERE branch_id = ? AND check_in_date BETWEEN ? AND ? AND status NOT IN ('CANCELLED','NO_SHOW')";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(avgSql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    result.put("averageStayDays", rs.getDouble("avg_stay"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public Map<String, Object> getStaffPerformance(Integer branchId, LocalDate from, LocalDate to) {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT u.id, u.first_name, u.last_name, COUNT(res.id) AS bookings_approved FROM users u LEFT JOIN reservations res ON res.approved_by = u.id AND res.approved_at::date BETWEEN ? AND ? WHERE u.branch_id = ? AND u.role_id IN (SELECT id FROM roles WHERE code IN ('MANAGER','RECEPTIONIST')) GROUP BY u.id, u.first_name, u.last_name";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setInt(3, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> staff = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("userId", rs.getInt("id"));
                    row.put("name", rs.getString("first_name") + " " + rs.getString("last_name"));
                    row.put("bookingsApproved", rs.getInt("bookings_approved"));
                    staff.add(row);
                }
                result.put("staff", staff);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Map<String, Object> getServiceStats(Integer branchId, LocalDate from, LocalDate to) {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT es.name, es.code, COUNT(re.id) AS times_booked, COALESCE(SUM(re.amount),0) AS revenue " +
                "FROM extra_services es LEFT JOIN reservation_extras re ON re.extra_id = es.id " +
                "LEFT JOIN reservations r ON re.reservation_id = r.id AND r.branch_id = ? AND r.check_in_date BETWEEN ? AND ? "
                +
                "WHERE es.is_active = true GROUP BY es.name, es.code ORDER BY revenue DESC";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> services = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", rs.getString("name"));
                    row.put("code", rs.getString("code"));
                    row.put("timesBooked", rs.getInt("times_booked"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    services.add(row);
                }
                result.put("services", services);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
