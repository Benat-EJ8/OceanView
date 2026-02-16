package com.oceanview.resort.repository;

import com.oceanview.resort.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Integer id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByRole(String roleCode);
    List<User> findByBranchId(Integer branchId);
    boolean save(User user);
    boolean update(User user);
    boolean deleteById(Integer id);
}
