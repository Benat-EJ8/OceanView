package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Notification;

import java.util.List;

public interface NotificationRepository {
    List<Notification> findByUserId(Integer userId);

    boolean save(Notification notification);

    boolean markAsRead(Integer id);

    int countUnreadByUserId(Integer userId);
}
