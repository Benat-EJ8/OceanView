package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(Integer id);
    List<Reservation> findByGuestId(Integer guestId);
    List<Reservation> findByBranchId(Integer branchId);
    List<Reservation> findByRoomIdAndOverlappingDates(Integer roomId, LocalDate checkIn, LocalDate checkOut);
    List<Reservation> findByStatus(String status);
    boolean save(Reservation r);
    boolean update(Reservation r);
}
