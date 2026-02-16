package com.oceanview.resort.patterns.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationSubject {
    private final List<NotificationObserver> observers = new ArrayList<>();

    public void attach(NotificationObserver o) {
        if (o != null && !observers.contains(o)) observers.add(o);
    }

    public void notifyObservers(NotificationEvent event) {
        for (NotificationObserver o : observers) {
            o.onNotification(event);
        }
    }
}
