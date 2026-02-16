package com.oceanview.resort.patterns.search;

import com.oceanview.resort.domain.Room;

import java.time.LocalDate;

/**
 * Specification pattern: Dynamic filters for room search.
 */
public interface RoomSpecification {
    boolean isSatisfiedBy(Room room, LocalDate checkIn, LocalDate checkOut);
}
