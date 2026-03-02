package com.oceanview.resort.repository;

import com.oceanview.resort.domain.Promotion;

import java.util.List;

public interface PromotionRepository {
    List<Promotion> findActivePromotions();
    boolean save(Promotion promotion);
}

