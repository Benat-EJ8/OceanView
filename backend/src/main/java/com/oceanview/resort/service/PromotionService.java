package com.oceanview.resort.service;

import com.oceanview.resort.domain.Promotion;
import com.oceanview.resort.repository.PromotionRepository;
import com.oceanview.resort.repository.PromotionRepositoryImpl;

import java.util.List;

public class PromotionService {
    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findActivePromotions();
    }

    public Promotion create(Promotion promotion) {
        boolean ok = promotionRepository.save(promotion);
        return ok ? promotion : null;
    }
}

