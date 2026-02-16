package com.oceanview.resort.domain;

import java.time.Instant;

public class MaintenanceRequest {
    private Integer id;
    private Integer roomId;
    private Integer reportedBy;
    private String title;
    private String description;
    private String priority;
    private String status;
    private Integer assignedTo;
    private Instant completedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Room room;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public Integer getReportedBy() { return reportedBy; }
    public void setReportedBy(Integer reportedBy) { this.reportedBy = reportedBy; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}
