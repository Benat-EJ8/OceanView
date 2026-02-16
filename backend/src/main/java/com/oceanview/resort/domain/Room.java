package com.oceanview.resort.domain;

import java.time.Instant;

public class Room {
    private Integer id;
    private Integer branchId;
    private Integer categoryId;
    private String roomNumber;
    private Integer floor;
    private String status;
    private String viewType;
    private Instant createdAt;
    private Instant updatedAt;
    private RoomCategory category;
    private Branch branch;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getViewType() { return viewType; }
    public void setViewType(String viewType) { this.viewType = viewType; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public RoomCategory getCategory() { return category; }
    public void setCategory(RoomCategory category) { this.category = category; }
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
}
