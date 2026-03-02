package com.oceanview.resort.service;

import com.oceanview.resort.domain.Notification;
import com.oceanview.resort.domain.User;
import com.oceanview.resort.repository.NotificationRepository;
import com.oceanview.resort.repository.NotificationRepositoryImpl;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.repository.UserRepositoryImpl;

import java.util.List;

public class NotificationService {
    private final NotificationRepository notificationRepository = new NotificationRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();

    public List<Notification> findByUserId(Integer userId) {
        return notificationRepository.findByUserId(userId);
    }

    public boolean markAsRead(Integer id) {
        return notificationRepository.markAsRead(id);
    }

    public int countUnread(Integer userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    public void notifyReceptionists(String type, String title, String body, String entityType, String entityId) {
        List<User> receptionists = userRepository.findByRole("RECEPTIONIST");
        List<User> managers = userRepository.findByRole("MANAGER");
        for (User u : receptionists) {
            createNotification(u.getId(), type, title, body, entityType, entityId);
        }
        for (User u : managers) {
            createNotification(u.getId(), type, title, body, entityType, entityId);
        }
    }

    private void createNotification(Integer userId, String type, String title, String body, String entityType,
            String entityId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setBody(body);
        n.setEntityType(entityType);
        n.setEntityId(entityId);
        n.setRead(false);
        notificationRepository.save(n);
    }
}
