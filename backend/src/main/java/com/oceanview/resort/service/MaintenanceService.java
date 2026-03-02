package com.oceanview.resort.service;

import com.oceanview.resort.domain.MaintenanceRequest;
import com.oceanview.resort.repository.MaintenanceRepository;
import com.oceanview.resort.repository.MaintenanceRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository = new MaintenanceRepositoryImpl();
    private final NotificationService notificationService = new NotificationService();

    public List<MaintenanceRequest> findAll() {
        return maintenanceRepository.findAll();
    }

    public List<MaintenanceRequest> findByRoomId(Integer roomId) {
        return maintenanceRepository.findByRoomId(roomId);
    }

    public List<MaintenanceRequest> findByReportedBy(Integer userId) {
        return maintenanceRepository.findByReportedBy(userId);
    }

    public Optional<MaintenanceRequest> create(MaintenanceRequest request) {
        if (maintenanceRepository.save(request)) {
            notificationService.notifyReceptionists(
                    "MAINTENANCE",
                    "New Maintenance Request",
                    "Room maintenance request: " + request.getTitle(),
                    "MAINTENANCE_REQUEST",
                    String.valueOf(request.getId()));
            return Optional.of(request);
        }
        return Optional.empty();
    }

    public boolean updateStatus(Integer id, String status) {
        return maintenanceRepository.updateStatus(id, status);
    }
}
