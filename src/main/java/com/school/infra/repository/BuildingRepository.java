package com.school.infra.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.school.infra.entity.Building;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    org.springframework.data.domain.Page<Building> findByDeletedFalse(
            org.springframework.data.domain.Pageable pageable);

    List<Building> findByDeletedFalse();
}
