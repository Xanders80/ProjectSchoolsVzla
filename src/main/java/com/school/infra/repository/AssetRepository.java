package com.school.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.infra.entity.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByRoomId(Long roomId);

    List<Asset> findByType(String type);
}
