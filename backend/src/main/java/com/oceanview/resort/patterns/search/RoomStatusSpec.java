package com.oceanview.resort.patterns.search;

import com.oceanview.resort.domain.Room;

import java.time.LocalDate;

public class RoomStatusSpec implements RoomSpecification {
    private final String status;

    public RoomStatusSpec(String status) {
        this.status = status;
    }

    @Override
    public boolean isSatisfiedBy(Room room, LocalDate checkIn, LocalDate checkOut) {
        return status == null || status.equals(room.getStatus());
    }
}
