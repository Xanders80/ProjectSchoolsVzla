package com.school.infra.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.infra.entity.MaintenanceRequest;
import com.school.infra.repository.MaintenanceRequestRepository;

@Service
@Transactional
public class MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceService(MaintenanceRequestRepository maintenanceRequestRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public Page<MaintenanceRequest> getAllRequests(@NonNull Pageable pageable) {
        return maintenanceRequestRepository.findAll(pageable);
    }

    public List<MaintenanceRequest> getAllRequests() {
        return maintenanceRequestRepository.findAll();
    }

    public Optional<MaintenanceRequest> getRequestById(@NonNull Long id) {
        return maintenanceRequestRepository.findById(id);
    }

    public MaintenanceRequest saveRequest(@NonNull MaintenanceRequest request) {
        if (request.getStatus() == null || request.getStatus().isEmpty()) {
            request.setStatus("PENDING");
        }
        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDateTime.now());
        }
        return maintenanceRequestRepository.save(request);
    }

    public void deleteRequest(@NonNull Long id) {
        maintenanceRequestRepository.deleteById(id);
    }

    public List<MaintenanceRequest> getRequestsByStatus(String status) {
        return maintenanceRequestRepository.findByStatus(status);
    }

    public List<MaintenanceRequest> getPendingRequests() {
        return maintenanceRequestRepository.findByStatus("PENDING");
    }

    public MaintenanceRequest updateStatus(@NonNull Long id, String newStatus) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        request.setStatus(newStatus);

        if ("COMPLETED".equals(newStatus) && request.getCompletionDate() == null) {
            request.setCompletionDate(LocalDateTime.now());
        }

        return maintenanceRequestRepository.save(request);
    }

    public long countByStatus(String status) {
        return maintenanceRequestRepository.countByStatus(status);
    }

    public long countPendingRequests() {
        return maintenanceRequestRepository.countByStatus("PENDING");
    }
}
