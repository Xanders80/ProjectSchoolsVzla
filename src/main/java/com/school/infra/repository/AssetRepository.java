package com.school.infra.repository;

import com.school.infra.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByRoomId(Long roomId);
}
