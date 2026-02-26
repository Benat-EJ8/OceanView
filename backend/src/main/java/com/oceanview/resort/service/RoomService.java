package com.oceanview.resort.service;

import com.oceanview.resort.domain.Room;
import com.oceanview.resort.domain.RoomCategory;
import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.mapper.RoomMapper;
import com.oceanview.resort.patterns.room.RoomFactory;
import com.oceanview.resort.patterns.room.RoomFactoryImpl;
import com.oceanview.resort.patterns.room.RoomStatusTransition;
import com.oceanview.resort.repository.RoomCategoryRepository;
import com.oceanview.resort.repository.RoomCategoryRepositoryImpl;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.RoomRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoomService {
    private final RoomRepository roomRepository = new RoomRepositoryImpl();
    private final RoomCategoryRepository categoryRepository = new RoomCategoryRepositoryImpl();
    private final RoomFactory roomFactory = new RoomFactoryImpl();

    public Optional<RoomDTO> findById(Integer id) {
        return roomRepository.findById(id).map(RoomMapper::toDTO);
    }

    public List<RoomDTO> findByBranchId(Integer branchId) {
        return roomRepository.findByBranchId(branchId).stream().map(RoomMapper::toDTO).collect(Collectors.toList());
    }

    public List<RoomDTO> findAvailable(Integer branchId, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableByBranchAndDates(branchId, checkIn, checkOut).stream().map(RoomMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<RoomCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Room> create(String roomNumber, int floor, Integer categoryId, Integer branchId) {
        RoomCategory cat = categoryRepository.findById(categoryId).orElse(null);
        if (cat == null)
            return Optional.empty();
        Room room = roomFactory.createRoom(roomNumber, floor, cat, branchId);
        return roomRepository.save(room) ? Optional.of(room) : Optional.empty();
    }

    public boolean updateStatus(Integer roomId, String newStatus) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null)
            return false;
        if (!RoomStatusTransition.canTransition(room.getStatus(), newStatus))
            return false;
        room.setStatus(newStatus);
        return roomRepository.update(room);
    }

    public boolean update(Room room) {
        return roomRepository.update(room);
    }

    public boolean delete(Integer id) {
        return roomRepository.delete(id);
    }
}
