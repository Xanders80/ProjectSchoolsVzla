package com.school.infra.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.school.infra.entity.Asset;
import com.school.infra.enums.AssetCategory;
import com.school.infra.enums.AssetCondition;
import com.school.infra.enums.AssetStatus;

/**
 * Repositorio para gestión de activos con queries avanzadas.
 * Soporta filtros por categoría, estado, condición y queries de reporting.
 */
public interface AssetRepository extends JpaRepository<Asset, Long> {

        // Queries básicas existentes
        List<Asset> findByRoomId(Long roomId);

        // Queries por enums
        List<Asset> findByCategory(AssetCategory category);

        List<Asset> findByStatus(AssetStatus status);

        List<Asset> findByCategoryAndStatus(AssetCategory category, AssetStatus status);

        List<Asset> findByCondition(AssetCondition condition);

        /**
         * Encuentra activos en condiciones que requieren mantenimiento (POOR, BROKEN).
         */
        @Query("SELECT a FROM Asset a WHERE a.condition IN :conditions")
        List<Asset> findByConditions(@Param("conditions") List<AssetCondition> conditions);

        /**
         * Encuentra activos con garantía vencida antes de una fecha.
         * Usa Native Query para asegurar compatibilidad con DATE_ADD de MariaDB.
         */
        @Query(value = "SELECT * FROM assets a WHERE a.purchase_date IS NOT NULL AND a.warranty_months IS NOT NULL " +
                        "AND DATE_ADD(a.purchase_date, INTERVAL a.warranty_months MONTH) < :date " +
                        "AND a.status = 'ACTIVE'", nativeQuery = true)
        List<Asset> findExpiredWarranties(@Param("date") LocalDate date);

        /**
         * Encuentra activos por número de serie (debe ser único).
         */
        Optional<Asset> findBySerialNumber(String serialNumber);

        /**
         * Encuentra activos con valor mayor al especificado.
         */
        @Query("SELECT a FROM Asset a WHERE a.purchaseValue >= :minValue")
        List<Asset> findByMinimumValue(@Param("minValue") BigDecimal minValue);

        // Queries de estadísticas y reporting

        /**
         * Cuenta activos por categoría.
         * Retorna array de Object[] con [AssetCategory, Long count].
         */
        @Query("SELECT a.category, COUNT(a) FROM Asset a GROUP BY a.category")
        List<Object[]> countByCategory();

        /**
         * Cuenta activos por estado.
         */
        @Query("SELECT a.status, COUNT(a) FROM Asset a GROUP BY a.status")
        List<Object[]> countByStatus();

        /**
         * Cuenta activos por condición.
         */
        @Query("SELECT a.condition, COUNT(a) FROM Asset a GROUP BY a.condition")
        List<Object[]> countByCondition();

        /**
         * Suma el valor total de activos por estado.
         */
        @Query("SELECT COALESCE(SUM(a.purchaseValue), 0) FROM Asset a WHERE a.status = :status")
        BigDecimal sumValueByStatus(@Param("status") AssetStatus status);

        /**
         * Suma el valor total de todos los activos activos.
         */
        @Query("SELECT COALESCE(SUM(a.purchaseValue), 0) FROM Asset a WHERE a.status = 'ACTIVE'")
        BigDecimal getTotalActiveAssetsValue();

        /**
         * Encuentra activos que necesitan mantenimiento basado en condición.
         */
        @Query("SELECT a FROM Asset a WHERE a.condition IN ('POOR', 'BROKEN') AND a.status = 'ACTIVE'")
        List<Asset> findAssetsNeedingMaintenance();

        /**
         * Encuentra activos que tienen mantenimiento activo (pendiente o en progreso).
         */
        @Query("SELECT DISTINCT a FROM Asset a JOIN a.maintenanceHistory m " +
                        "WHERE m.status IN ('PENDING', 'IN_PROGRESS')")
        List<Asset> findAssetsWithActiveMaintenance();
}
