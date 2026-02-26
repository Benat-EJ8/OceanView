package com.oceanview.resort.repository;

import com.oceanview.resort.domain.ServiceRequest;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository {
    List<ServiceRequest> findAll();

    List<ServiceRequest> findByGuestId(Integer guestId);

    Optional<ServiceRequest> findById(Integer id);

    boolean save(ServiceRequest request);

    boolean updateStatus(Integer id, String status);
}
