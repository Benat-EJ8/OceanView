package com.oceanview.resort.patterns.observer;


 //Observer for activity logging (user actions).

public interface ActivityObserver {
    void onActivity(String action, String entityType, String entityId, Object details);
}
