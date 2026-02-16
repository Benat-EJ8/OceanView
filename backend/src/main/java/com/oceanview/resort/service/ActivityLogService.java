package com.oceanview.resort.service;

import com.oceanview.resort.domain.ActivityLog;
import com.oceanview.resort.repository.ActivityLogRepository;
import com.oceanview.resort.repository.ActivityLogRepositoryImpl;
import com.oceanview.resort.util.JsonHelper;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityLogService {
    private final ActivityLogRepository repo = new ActivityLogRepositoryImpl();

    public void log(Integer userId, String action, String entityType, String entityId, String ipAddress) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setIpAddress(ipAddress);
        repo.save(log);
    }

    public void log(Integer userId, String action, String entityType, String entityId, Object details, String ipAddress) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetailsJson(details != null ? JsonHelper.toJson(details) : null);
        log.setIpAddress(ipAddress);
        repo.save(log);
    }

    public List<ActivityLog> findByUserId(Integer userId, int limit) {
        return repo.findByUserId(userId, limit);
    }
}
