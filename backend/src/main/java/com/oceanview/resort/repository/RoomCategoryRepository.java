package com.oceanview.resort.repository;

import com.oceanview.resort.domain.RoomCategory;

import java.util.List;
import java.util.Optional;

public interface RoomCategoryRepository {
    Optional<RoomCategory> findById(Integer id);
    List<RoomCategory> findAll();
}
