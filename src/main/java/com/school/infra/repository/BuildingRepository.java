package com.school.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.infra.entity.Building;

public interface BuildingRepository extends JpaRepository<Building, Long> {}
