package com.school.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.infra.entity.MaintenanceRequest;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByStatus(String status);

    long countByStatus(String status);
}
