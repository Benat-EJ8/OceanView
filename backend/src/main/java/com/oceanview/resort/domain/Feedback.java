package com.oceanview.resort.domain;

import java.time.Instant;

public class Feedback {
    private Integer id;
    private Integer guestId;
    private Integer reservationId;
    private Integer rating;
    private String comment;
    private String category;
    private Boolean public_visible;
    private Instant createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getGuestId() { return guestId; }
    public void setGuestId(Integer guestId) { this.guestId = guestId; }
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Boolean getPublicVisible() { return public_visible; }
    public void setPublicVisible(Boolean public_visible) { this.public_visible = public_visible; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
