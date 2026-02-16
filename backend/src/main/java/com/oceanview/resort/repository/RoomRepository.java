package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository {
    Optional<Room> findById(Integer id);
    List<Room> findByBranchId(Integer branchId);
    List<Room> findByCategoryId(Integer categoryId);
    List<Room> findAvailableByBranchAndDates(Integer branchId, LocalDate checkIn, LocalDate checkOut);
    boolean save(Room room);
    boolean update(Room room);
}
