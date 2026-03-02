package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Feedback;

import java.util.List;

public interface FeedbackRepository {
    List<Feedback> findAll();

    List<Feedback> findByGuestId(Integer guestId);

    boolean save(Feedback feedback);
}
