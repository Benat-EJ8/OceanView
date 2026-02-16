package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Guest;

import java.util.List;
import java.util.Optional;

public interface GuestRepository {
    Optional<Guest> findById(Integer id);
    Optional<Guest> findByUserId(Integer userId);
    Optional<Guest> findByEmail(String email);
    List<Guest> findAll();
    boolean save(Guest guest);
    boolean update(Guest guest);
}
