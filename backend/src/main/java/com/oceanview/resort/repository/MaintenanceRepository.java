package com.oceanview.resort.repository;

import com.oceanview.resort.domain.MaintenanceRequest;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository {
    List<MaintenanceRequest> findAll();

    List<MaintenanceRequest> findByRoomId(Integer roomId);

    List<MaintenanceRequest> findByReportedBy(Integer userId);

    Optional<MaintenanceRequest> findById(Integer id);

    boolean save(MaintenanceRequest request);

    boolean updateStatus(Integer id, String status);
}
