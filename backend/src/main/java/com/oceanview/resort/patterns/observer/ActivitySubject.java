package com.oceanview.resort.patterns.observer;

import java.util.ArrayList;
import java.util.List;

//Subject for activity observers (Observer pattern).

public class ActivitySubject {
    private final List<ActivityObserver> observers = new ArrayList<>();

    public void attach(ActivityObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void detach(ActivityObserver observer) {
        observers.remove(observer);
    }

    public void notifyActivity(String action, String entityType, String entityId, Object details) {
        for (ActivityObserver o : observers) {
            o.onActivity(action, entityType, entityId, details);
        }
    }
}
