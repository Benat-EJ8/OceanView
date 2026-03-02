package com.oceanview.resort.service;

import com.oceanview.resort.domain.ServiceRequest;
import com.oceanview.resort.repository.ServiceRequestRepository;
import com.oceanview.resort.repository.ServiceRequestRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository = new ServiceRequestRepositoryImpl();
    private final NotificationService notificationService = new NotificationService();

    public List<ServiceRequest> findAll() {
        return serviceRequestRepository.findAll();
    }

    public List<ServiceRequest> findByGuestId(Integer guestId) {
        return serviceRequestRepository.findByGuestId(guestId);
    }

    public Optional<ServiceRequest> create(ServiceRequest request) {
        if (serviceRequestRepository.save(request)) {
            notificationService.notifyReceptionists(
                    "SERVICE_REQUEST",
                    "New Service Request",
                    request.getRequestType() + ": "
                            + (request.getDescription() != null ? request.getDescription() : ""),
                    "SERVICE_REQUEST",
                    String.valueOf(request.getId()));
            return Optional.of(request);
        }
        return Optional.empty();
    }

    public boolean updateStatus(Integer id, String status) {
        return serviceRequestRepository.updateStatus(id, status);
    }
}
