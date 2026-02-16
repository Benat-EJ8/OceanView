package com.oceanview.resort.mapper;

import com.oceanview.resort.domain.Room;
import com.oceanview.resort.dto.RoomDTO;

public final class RoomMapper {
    private RoomMapper() {}

    public static RoomDTO toDTO(Room r) {
        if (r == null) return null;
        RoomDTO dto = new RoomDTO();
        dto.setId(r.getId());
        dto.setBranchId(r.getBranchId());
        dto.setCategoryId(r.getCategoryId());
        dto.setRoomNumber(r.getRoomNumber());
        dto.setFloor(r.getFloor());
        dto.setStatus(r.getStatus());
        dto.setViewType(r.getViewType());
        if (r.getCategory() != null) {
            dto.setCategoryName(r.getCategory().getName());
            dto.setCategoryCode(r.getCategory().getCode());
            dto.setBasePrice(r.getCategory().getBasePrice());
            dto.setMaxOccupancy(r.getCategory().getMaxOccupancy());
        }
        return dto;
    }
}
