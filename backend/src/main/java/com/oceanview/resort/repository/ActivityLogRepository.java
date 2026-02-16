package com.oceanview.resort.repository;

import com.oceanview.resort.domain.ActivityLog;

import java.util.List;

public interface ActivityLogRepository {
    void save(ActivityLog log);
    List<ActivityLog> findByUserId(Integer userId, int limit);
}
