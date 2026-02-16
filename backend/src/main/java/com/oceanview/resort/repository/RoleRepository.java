package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findById(Integer id);
    Optional<Role> findByCode(String code);
}
