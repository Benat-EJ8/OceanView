package com.oceanview.resort.dto;

import java.math.BigDecimal;

public class RoomDTO {
    private Integer id;
    private Integer branchId;
    private Integer categoryId;
    private String roomNumber;
    private Integer floor;
    private String status;
    private String viewType;
    private String categoryName;
    private String categoryCode;
    private BigDecimal basePrice;
    private Integer maxOccupancy;

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
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public Integer getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(Integer maxOccupancy) { this.maxOccupancy = maxOccupancy; }
}
