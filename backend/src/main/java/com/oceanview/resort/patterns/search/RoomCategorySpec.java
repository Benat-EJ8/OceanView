package com.oceanview.resort.patterns.search;

import com.oceanview.resort.domain.Room;

import java.time.LocalDate;

public class RoomCategorySpec implements RoomSpecification {
    private final Integer categoryId;

    public RoomCategorySpec(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean isSatisfiedBy(Room room, LocalDate checkIn, LocalDate checkOut) {
        return categoryId == null || categoryId.equals(room.getCategoryId());
    }
}
