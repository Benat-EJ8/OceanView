package com.oceanview.resort.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class RoomCategory {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer maxOccupancy;
    private BigDecimal sizeSqm;
    private String amenitiesJson;
    private String imageUrl;
    private Instant createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public Integer getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(Integer maxOccupancy) { this.maxOccupancy = maxOccupancy; }
    public BigDecimal getSizeSqm() { return sizeSqm; }
    public void setSizeSqm(BigDecimal sizeSqm) { this.sizeSqm = sizeSqm; }
    public String getAmenitiesJson() { return amenitiesJson; }
    public void setAmenitiesJson(String amenitiesJson) { this.amenitiesJson = amenitiesJson; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
