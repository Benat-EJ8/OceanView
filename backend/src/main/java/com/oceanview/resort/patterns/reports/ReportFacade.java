package com.oceanview.resort.patterns.reports;

import java.time.LocalDate;
import java.util.Map;

/**
 * Facade: Simplified reporting API for occupancy, revenue, utilization, etc.
 */
public interface ReportFacade {
    Map<String, Object> getOccupancyReport(Integer branchId, LocalDate from, LocalDate to);
    Map<String, Object> getRevenueReport(Integer branchId, LocalDate from, LocalDate to);
    Map<String, Object> getBookingStats(Integer branchId, LocalDate from, LocalDate to);
    Map<String, Object> getStaffPerformance(Integer branchId, LocalDate from, LocalDate to);
}
