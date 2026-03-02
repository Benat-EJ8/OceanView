package com.oceanview.resort.service;

import com.oceanview.resort.domain.Feedback;
import com.oceanview.resort.repository.FeedbackRepository;
import com.oceanview.resort.repository.FeedbackRepositoryImpl;

import java.util.List;

public class FeedbackService {
    private final FeedbackRepository feedbackRepository = new FeedbackRepositoryImpl();

    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    public List<Feedback> findByGuestId(Integer guestId) {
        return feedbackRepository.findByGuestId(guestId);
    }

    public boolean create(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
}
