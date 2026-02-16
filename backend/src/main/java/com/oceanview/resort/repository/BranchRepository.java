package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Branch;

import java.util.List;
import java.util.Optional;

public interface BranchRepository {
    Optional<Branch> findById(Integer id);
    List<Branch> findAll();
    List<Branch> findByParentId(Integer parentId);
}
