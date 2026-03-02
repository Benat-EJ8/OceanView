package com.oceanview.resort.service;

import com.oceanview.resort.util.DataSourceProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class BillingService {
    private final DataSourceProvider ds = DataSourceProvider.getInstance();
    private static final BigDecimal TAX_RATE = new BigDecimal("10.00"); // 10% tax

    public Map<String, Object> generateInvoice(Integer reservationId) {
        try (Connection conn = ds.getConnection()) {
            // Check if invoice already exists
            Map<String, Object> existing = getInvoiceByReservation(reservationId, conn);
            if (existing != null && !existing.isEmpty())
                return existing;

            // Get reservation details
            Map<String, Object> reservation = getReservationDetails(reservationId, conn);
            if (reservation == null)
                return null;

            LocalDate checkIn = ((java.sql.Date) reservation.get("checkInDate")).toLocalDate();
            LocalDate checkOut = ((java.sql.Date) reservation.get("checkOutDate")).toLocalDate();
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (nights <= 0)
                nights = 1;

            BigDecimal roomPrice = (BigDecimal) reservation.get("basePrice");
            BigDecimal roomTotal = roomPrice.multiply(BigDecimal.valueOf(nights));

            // Get extra services
            List<Map<String, Object>> extras = getReservationExtras(reservationId, conn);
            BigDecimal extrasTotal = extras.stream()
                    .map(e -> (BigDecimal) e.get("amount"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal subtotal = roomTotal.add(extrasTotal);
            BigDecimal taxAmount = subtotal.multiply(TAX_RATE).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal totalAmount = subtotal.add(taxAmount);

            // Generate invoice number
            String invoiceNumber = "INV-" + reservationId + "-" + System.currentTimeMillis();

            // Insert invoice
            String sql = "INSERT INTO invoices (reservation_id, invoice_number, subtotal, tax_rate, tax_amount, discount_amount, total_amount, status) VALUES (?,?,?,?,?,?,?,?) RETURNING id";
            int invoiceId;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, reservationId);
                ps.setString(2, invoiceNumber);
                ps.setBigDecimal(3, subtotal);
                ps.setBigDecimal(4, TAX_RATE);
                ps.setBigDecimal(5, taxAmount);
                ps.setBigDecimal(6, BigDecimal.ZERO);
                ps.setBigDecimal(7, totalAmount);
                ps.setString(8, "PENDING");
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    invoiceId = rs.getInt(1);
                }
            }

            // Insert line items - room charge
            insertLineItem(conn, invoiceId,
                    "Room - " + reservation.get("roomNumber") + " (" + reservation.get("categoryName") + ")", nights,
                    roomPrice, roomTotal, "ROOM");

            // Insert line items - extras
            for (Map<String, Object> extra : extras) {
                insertLineItem(conn, invoiceId, (String) extra.get("name"),
                        ((Integer) extra.get("quantity")).longValue(),
                        (BigDecimal) extra.get("unitPrice"),
                        (BigDecimal) extra.get("amount"), "SERVICE");
            }

            return getInvoice(invoiceId, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getInvoiceForReservation(Integer reservationId) {
        try (Connection conn = ds.getConnection()) {
            Map<String, Object> invoice = getInvoiceByReservation(reservationId, conn);
            return invoice != null ? invoice : Collections.emptyMap();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertLineItem(Connection conn, int invoiceId, String description, long quantity, BigDecimal unitPrice,
            BigDecimal amount, String lineType) throws SQLException {
        String sql = "INSERT INTO invoice_line_items (invoice_id, description, quantity, unit_price, amount, line_type) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setString(2, description);
            ps.setBigDecimal(3, BigDecimal.valueOf(quantity));
            ps.setBigDecimal(4, unitPrice);
            ps.setBigDecimal(5, amount);
            ps.setString(6, lineType);
            ps.executeUpdate();
        }
    }

    private Map<String, Object> getReservationDetails(Integer reservationId, Connection conn) throws SQLException {
        String sql = "SELECT r.check_in_date, r.check_out_date, rm.room_number, rc.name AS category_name, rc.base_price "
                +
                "FROM reservations r JOIN rooms rm ON r.room_id = rm.id JOIN room_categories rc ON rm.category_id = rc.id WHERE r.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                Map<String, Object> row = new HashMap<>();
                row.put("checkInDate", rs.getDate("check_in_date"));
                row.put("checkOutDate", rs.getDate("check_out_date"));
                row.put("roomNumber", rs.getString("room_number"));
                row.put("categoryName", rs.getString("category_name"));
                row.put("basePrice", rs.getBigDecimal("base_price"));
                return row;
            }
        }
    }

    private List<Map<String, Object>> getReservationExtras(Integer reservationId, Connection conn) throws SQLException {
        String sql = "SELECT es.name, re.quantity, re.unit_price, re.amount FROM reservation_extras re JOIN extra_services es ON re.extra_id = es.id WHERE re.reservation_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> extras = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", rs.getString("name"));
                    row.put("quantity", rs.getInt("quantity"));
                    row.put("unitPrice", rs.getBigDecimal("unit_price"));
                    row.put("amount", rs.getBigDecimal("amount"));
                    extras.add(row);
                }
                return extras;
            }
        }
    }

    private Map<String, Object> getInvoiceByReservation(Integer reservationId, Connection conn) throws SQLException {
        String sql = "SELECT id FROM invoices WHERE reservation_id = ? ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return getInvoice(rs.getInt("id"), conn);
            }
        }
    }

    private Map<String, Object> getInvoice(Integer invoiceId, Connection conn) throws SQLException {
        Map<String, Object> invoice = new HashMap<>();
        String sql = "SELECT i.*, r.check_in_date, r.check_out_date, r.guest_id, g.first_name, g.last_name, g.email, rm.room_number, rc.name AS category_name "
                +
                "FROM invoices i JOIN reservations r ON i.reservation_id = r.id JOIN guests g ON r.guest_id = g.id " +
                "LEFT JOIN rooms rm ON r.room_id = rm.id LEFT JOIN room_categories rc ON rm.category_id = rc.id WHERE i.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                invoice.put("id", rs.getInt("id"));
                invoice.put("reservationId", rs.getInt("reservation_id"));
                invoice.put("invoiceNumber", rs.getString("invoice_number"));
                invoice.put("subtotal", rs.getBigDecimal("subtotal"));
                invoice.put("taxRate", rs.getBigDecimal("tax_rate"));
                invoice.put("taxAmount", rs.getBigDecimal("tax_amount"));
                invoice.put("discountAmount", rs.getBigDecimal("discount_amount"));
                invoice.put("totalAmount", rs.getBigDecimal("total_amount"));
                invoice.put("status", rs.getString("status"));
                invoice.put("guestName", rs.getString("first_name") + " " + rs.getString("last_name"));
                invoice.put("guestEmail", rs.getString("email"));
                invoice.put("roomNumber", rs.getString("room_number"));
                invoice.put("categoryName", rs.getString("category_name"));
                invoice.put("checkInDate",
                        rs.getDate("check_in_date") != null ? rs.getDate("check_in_date").toString() : null);
                invoice.put("checkOutDate",
                        rs.getDate("check_out_date") != null ? rs.getDate("check_out_date").toString() : null);
            }
        }

        // Get line items
        String linesSql = "SELECT * FROM invoice_line_items WHERE invoice_id = ? ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(linesSql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> items = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("description", rs.getString("description"));
                    item.put("quantity", rs.getBigDecimal("quantity"));
                    item.put("unitPrice", rs.getBigDecimal("unit_price"));
                    item.put("amount", rs.getBigDecimal("amount"));
                    item.put("lineType", rs.getString("line_type"));
                    items.add(item);
                }
                invoice.put("lineItems", items);
            }
        }
        return invoice;
    }
}
