package com.oceanview.resort.patterns.room;

import com.oceanview.resort.domain.Room;
import com.oceanview.resort.domain.RoomCategory;

/**
 * Factory: Room creation with category-based defaults.
 */
public interface RoomFactory {
    Room createRoom(String roomNumber, int floor, RoomCategory category, Integer branchId);
}
