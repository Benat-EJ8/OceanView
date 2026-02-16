package com.oceanview.resort.mapper;

import com.oceanview.resort.domain.Reservation;
import com.oceanview.resort.dto.ReservationDTO;

public final class ReservationMapper {
    private ReservationMapper() {}

    public static ReservationDTO toDTO(Reservation r) {
        if (r == null) return null;
        ReservationDTO dto = new ReservationDTO();
        dto.setId(r.getId());
        dto.setBranchId(r.getBranchId());
        dto.setGuestId(r.getGuestId());
        dto.setRoomId(r.getRoomId());
        dto.setStatus(r.getStatus());
        dto.setCheckInDate(r.getCheckInDate());
        dto.setCheckOutDate(r.getCheckOutDate());
        dto.setAdults(r.getAdults());
        dto.setChildren(r.getChildren());
        dto.setSpecialRequests(r.getSpecialRequests());
        dto.setDepositAmount(r.getDepositAmount());
        if (r.getGuest() != null) {
            dto.setGuestName(r.getGuest().getFirstName() + " " + r.getGuest().getLastName());
        }
        if (r.getRoom() != null) {
            dto.setRoomNumber(r.getRoom().getRoomNumber());
            if (r.getRoom().getCategory() != null) {
                dto.setCategoryName(r.getRoom().getCategory().getName());
            }
        }
        return dto;
    }
}
