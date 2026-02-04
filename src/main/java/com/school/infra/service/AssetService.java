package com.school.infra.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.core.exception.BusinessValidationException;
import com.school.infra.entity.Asset;
import com.school.infra.enums.AssetCategory;
import com.school.infra.enums.AssetCondition;
import com.school.infra.enums.AssetStatus;
import com.school.infra.enums.MaintenanceStatus;
import com.school.infra.repository.AssetRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Servicio de gestión de activos con validaciones de negocio robustas.
 * Implementa reglas SOLID: Single Responsibility, validaciones centralizadas.
 */
@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    // ============ CRUD Básico ============

    public Page<Asset> getAllAssets(@NonNull Pageable pageable) {
        return assetRepository.findAll(pageable);
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Optional<Asset> getAssetById(@NonNull Long id) {
        return assetRepository.findById(id);
    }

    /**
     * Crea un nuevo activo con validaciones de negocio.
     */
    public Asset createAsset(@NonNull Asset asset) {
        validateAsset(asset);

        // Verificar unicidad de serial number si está presente
        if (asset.getSerialNumber() != null && !asset.getSerialNumber().isBlank()) {
            Optional<Asset> existing = assetRepository.findBySerialNumber(asset.getSerialNumber());
            if (existing.isPresent()) {
                throw new BusinessValidationException(
                        "Ya existe un activo con el número de serie: " + asset.getSerialNumber());
            }
        }

        // Establecer estado inicial si no está definido
        if (asset.getStatus() == null) {
            asset.setStatus(AssetStatus.ACTIVE);
        }

        if (asset.getCondition() == null) {
            asset.setCondition(AssetCondition.GOOD);
        }

        return assetRepository.save(asset);
    }

    /**
     * Actualiza un activo existente con validaciones.
     */
    public Asset updateAsset(@NonNull Long id, @NonNull Asset updatedAsset) {
        Asset existing = getAssetById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activo no encontrado con ID: " + id));

        validateAsset(updatedAsset);

        // Verificar unicidad de serial number (excluyendo el propio activo)
        if (updatedAsset.getSerialNumber() != null && !updatedAsset.getSerialNumber().isBlank()) {
            Optional<Asset> withSameSerial = assetRepository.findBySerialNumber(updatedAsset.getSerialNumber());
            if (withSameSerial.isPresent() && !withSameSerial.get().getId().equals(id)) {
                throw new BusinessValidationException(
                        "Ya existe otro activo con el número de serie: " + updatedAsset.getSerialNumber());
            }
        }

        // Actualizar campos
        existing.setName(updatedAsset.getName());
        existing.setSerialNumber(updatedAsset.getSerialNumber());
        existing.setCategory(updatedAsset.getCategory());
        existing.setStatus(updatedAsset.getStatus());
        existing.setCondition(updatedAsset.getCondition());
        existing.setPurchaseDate(updatedAsset.getPurchaseDate());
        existing.setPurchaseValue(updatedAsset.getPurchaseValue());
        existing.setDepreciationRate(updatedAsset.getDepreciationRate());
        existing.setManufacturer(updatedAsset.getManufacturer());
        existing.setModel(updatedAsset.getModel());
        existing.setWarrantyMonths(updatedAsset.getWarrantyMonths());
        existing.setRoom(updatedAsset.getRoom());

        return assetRepository.save(existing);
    }

    public void deleteAsset(@NonNull Long id) {
        Asset asset = getAssetById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activo no encontrado con ID: " + id));

        // Validar que no tenga mantenimientos activos
        if (asset.hasActiveMaintenante()) {
            throw new BusinessValidationException(
                    "No se puede eliminar el activo porque tiene solicitudes de mantenimiento activas");
        }

        assetRepository.deleteById(id);
    }

    // ============ Cambio de Estado con Validaciones ============

    /**
     * Cambia el estado de un activo con validaciones de coherencia.
     */
    public Asset updateStatus(@NonNull Long id, @NonNull AssetStatus newStatus) {
        Asset asset = getAssetById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activo no encontrado con ID: " + id));

        AssetStatus currentStatus = asset.getStatus();

        // Validaciones de coherencia de negocio
        switch (newStatus) {
            case IN_MAINTENANCE:
                // Solo se puede marcar en mantenimiento si tiene solicitudes activas
                if (!asset.hasActiveMaintenante()) {
                    throw new BusinessValidationException(
                            "No se puede cambiar a 'En Mantenimiento' sin una solicitud de mantenimiento activa");
                }
                break;

            case RETIRED:
            case LOST:
                // No permitir retirar/extraviar si tiene mantenimiento activo
                if (asset.hasActiveMaintenante()) {
                    throw new BusinessValidationException(
                            "No se puede cambiar a '" + newStatus.getDisplayName() +
                                    "' mientras tiene mantenimiento activo");
                }
                break;

            case ACTIVE:
                // Verificar que no esté en condición rota para reactivar
                if (asset.getCondition() == AssetCondition.BROKEN) {
                    throw new BusinessValidationException(
                            "No se puede activar un activo en condición 'Roto'. Debe repararse primero.");
                }
                break;

            default:
                break;
        }

        asset.setStatus(newStatus);
        return assetRepository.save(asset);
    }

    /**
     * Cambia la condición del activo y ajusta su estado si es necesario.
     */
    public Asset updateCondition(@NonNull Long id, @NonNull AssetCondition newCondition) {
        Asset asset = getAssetById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activo no encontrado con ID: " + id));

        asset.setCondition(newCondition);

        // Si la condición es crítica y está activo, marcarlo como dañado
        if (newCondition.requiresMaintenance() && asset.getStatus() == AssetStatus.ACTIVE) {
            asset.setStatus(AssetStatus.DAMAGED);
        }

        return assetRepository.save(asset);
    }

    // ============ Consultas y Filtros ============

    public List<Asset> getAssetsByCategory(@NonNull AssetCategory category) {
        return assetRepository.findByCategory(category);
    }

    public List<Asset> getAssetsByStatus(@NonNull AssetStatus status) {
        return assetRepository.findByStatus(status);
    }

    public List<Asset> getAssetsByCondition(@NonNull AssetCondition condition) {
        return assetRepository.findByCondition(condition);
    }

    public List<Asset> filterAssets(AssetCategory category, AssetStatus status) {
        if (category != null && status != null) {
            return assetRepository.findByCategoryAndStatus(category, status);
        } else if (category != null) {
            return getAssetsByCategory(category);
        } else if (status != null) {
            return getAssetsByStatus(status);
        } else {
            return getAllAssets();
        }
    }

    /**
     * Obtiene activos que necesitan mantenimiento (condición POOR o BROKEN).
     */
    public List<Asset> getAssetsNeedingMaintenance() {
        return assetRepository.findByConditions(
                Arrays.asList(AssetCondition.POOR, AssetCondition.BROKEN));
    }

    /**
     * Obtiene activos con garantía vencida.
     */
    public List<Asset> getAssetsWithExpiredWarranty() {
        return assetRepository.findExpiredWarranties(LocalDate.now());
    }

    /**
     * Obtiene activos con mantenimiento activo.
     */
    public List<Asset> getAssetsWithActiveMaintenance() {
        return assetRepository.findAssetsWithActiveMaintenance();
    }

    // ============ Reportes y Estadísticas ============

    /**
     * Obtiene distribución de activos por categoría.
     *
     * @return Mapa con categoría y cantidad
     */
    public Map<AssetCategory, Long> getAssetsByCategory() {
        return assetRepository.countByCategory().stream()
                .filter(arr -> arr[0] != null) // Ignorar categorías nulas
                .collect(Collectors.toMap(
                        arr -> (AssetCategory) arr[0],
                        arr -> (Long) arr[1]));
    }

    /**
     * Obtiene distribución de activos por estado.
     */
    public Map<AssetStatus, Long> getAssetsByStatus() {
        return assetRepository.countByStatus().stream()
                .filter(arr -> arr[0] != null) // Ignorar estados nulos
                .collect(Collectors.toMap(
                        arr -> (AssetStatus) arr[0],
                        arr -> (Long) arr[1]));
    }

    /**
     * Obtiene distribución de activos por condición.
     */
    public Map<AssetCondition, Long> getAssetsByCondition() {
        return assetRepository.countByCondition().stream()
                .collect(Collectors.toMap(
                        arr -> (AssetCondition) arr[0],
                        arr -> (Long) arr[1]));
    }

    /**
     * Calcula el valor total de activos activos.
     */
    public BigDecimal getTotalActiveAssetsValue() {
        BigDecimal total = assetRepository.getTotalActiveAssetsValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Calcula el valor total de activos por estado.
     */
    public BigDecimal getTotalValueByStatus(@NonNull AssetStatus status) {
        BigDecimal value = assetRepository.sumValueByStatus(status);
        return value != null ? value : BigDecimal.ZERO;
    }

    public long countAssets() {
        return assetRepository.count();
    }

    public long countAssetsByStatus(@NonNull AssetStatus status) {
        return assetRepository.findByStatus(status).size();
    }

    // ============ Validaciones Privadas ============

    /**
     * Valida reglas de negocio para un activo.
     */
    private void validateAsset(Asset asset) {
        if (asset == null) {
            throw new BusinessValidationException("El activo no puede ser nulo");
        }

        if (asset.getName() == null || asset.getName().isBlank()) {
            throw new BusinessValidationException("El nombre del activo es obligatorio");
        }

        if (asset.getCategory() == null) {
            throw new BusinessValidationException("La categoría es obligatoria");
        }

        // Validar valor de compra si está presente
        if (asset.getPurchaseValue() != null) {
            if (asset.getPurchaseValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessValidationException("El valor de compra debe ser positivo");
            }
        }

        // Validar tasa de depreciación
        if (asset.getDepreciationRate() != null) {
            if (asset.getDepreciationRate().compareTo(BigDecimal.ZERO) < 0 ||
                    asset.getDepreciationRate().compareTo(BigDecimal.ONE) > 0) {
                throw new BusinessValidationException(
                        "La tasa de depreciación debe estar entre 0 y 1 (0% - 100%)");
            }
        }

        // Validar meses de garantía
        if (asset.getWarrantyMonths() != null && asset.getWarrantyMonths() < 0) {
            throw new BusinessValidationException("Los meses de garantía no pueden ser negativos");
        }
    }
}
