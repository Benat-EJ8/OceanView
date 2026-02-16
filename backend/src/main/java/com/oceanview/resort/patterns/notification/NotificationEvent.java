package com.oceanview.resort.patterns.notification;

public class NotificationEvent {
    private final String type;
    private final String title;
    private final String body;
    private final Integer userId;
    private final Integer guestId;
    private final String entityType;
    private final String entityId;

    public NotificationEvent(String type, String title, String body, Integer userId, Integer guestId, String entityType, String entityId) {
        this.type = type;
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.guestId = guestId;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public Integer getUserId() { return userId; }
    public Integer getGuestId() { return guestId; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
}
