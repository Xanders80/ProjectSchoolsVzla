package com.school.infra.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.infra.entity.Asset;
import com.school.infra.repository.AssetRepository;

@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public Page<Asset> getAllAssets(@NonNull Pageable pageable) {
        return assetRepository.findAll(pageable);
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Optional<Asset> getAssetById(@NonNull Long id) {
        return assetRepository.findById(id);
    }

    public Asset saveAsset(@NonNull Asset asset) {
        return assetRepository.save(asset);
    }

    public void deleteAsset(@NonNull Long id) {
        assetRepository.deleteById(id);
    }

    public List<Asset> getAssetsByType(String type) {
        return assetRepository.findByType(type);
    }

    public long countAssets() {
        return assetRepository.count();
    }
}
