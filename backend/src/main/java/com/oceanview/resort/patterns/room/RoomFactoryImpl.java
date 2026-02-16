package com.oceanview.resort.patterns.room;

import com.oceanview.resort.domain.Room;
import com.oceanview.resort.domain.RoomCategory;

public class RoomFactoryImpl implements RoomFactory {
    @Override
    public Room createRoom(String roomNumber, int floor, RoomCategory category, Integer branchId) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setFloor(floor);
        room.setCategoryId(category != null ? category.getId() : null);
        room.setCategory(category);
        room.setBranchId(branchId);
        room.setStatus("AVAILABLE");
        return room;
    }
}
