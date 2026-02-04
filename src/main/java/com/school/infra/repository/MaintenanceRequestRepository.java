package com.school.infra.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.school.infra.entity.MaintenanceRequest;
import com.school.infra.enums.MaintenanceStatus;
import com.school.infra.enums.MaintenancePriority;
import com.school.infra.enums.MaintenanceType;

/**
 * Repositorio para gestión de solicitudes de mantenimiento.
 * Incluye queries para workflow, tracking de SLA y reporting.
 */
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    // Queries básicas por estado, prioridad y tipo

    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);

    long countByStatus(MaintenanceStatus status);

    List<MaintenanceRequest> findByPriority(MaintenancePriority priority);

    List<MaintenanceRequest> findByType(MaintenanceType type);

    // Queries por relaciones

    /**
     * Encuentra solicitudes por activo específico.
     */
    List<MaintenanceRequest> findByAssetId(Long assetId);

    /**
     * Encuentra solicitudes por ubicación.
     */
    List<MaintenanceRequest> findByRoomId(Long roomId);

    /**
     * Encuentra solicitudes asignadas a un técnico específico.
     */
    List<MaintenanceRequest> findByAssignedToId(Long userId);

    /**
     * Encuentra solicitudes creadas por un usuario.
     */
    List<MaintenanceRequest> findByRequestedById(Long userId);

    // Queries de SLA y vencimientos

    /**
     * Encuentra solicitudes vencidas (pasado el due date) en un estado específico.
     */
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.status = :status AND m.dueDate < :date")
    List<MaintenanceRequest> findOverdueByStatus(
            @Param("status") MaintenanceStatus status,
            @Param("date") LocalDate date);

    /**
     * Encuentra todas las solicitudes vencidas (no finalizadas).
     */
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND m.dueDate < :date")
    List<MaintenanceRequest> findAllOverdue(@Param("date") LocalDate date);

    /**
     * Encuentra solicitudes próximas a vencer (en los próximos N días).
     */
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.status IN ('PENDING', 'IN_PROGRESS') " +
            "AND m.dueDate BETWEEN :today AND :futureDate")
    List<MaintenanceRequest> findNearDueDate(
            @Param("today") LocalDate today,
            @Param("futureDate") LocalDate futureDate);

    // Queries de historial y análisis

    /**
     * Obtiene el historial completo de mantenimiento de un activo ordenado por
     * fecha.
     */
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.asset.id = :assetId " +
            "ORDER BY m.requestDate DESC")
    List<MaintenanceRequest> findMaintenanceHistory(@Param("assetId") Long assetId);

    /**
     * Encuentra solicitudes completadas en un rango de fechas.
     */
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.status = 'COMPLETED' " +
            "AND m.completionDate BETWEEN :startDate AND :endDate")
    List<MaintenanceRequest> findCompletedBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Queries de estadísticas y reportes

    /**
     * Cuenta solicitudes por estado.
     */
    @Query("SELECT m.status, COUNT(m) FROM MaintenanceRequest m GROUP BY m.status")
    List<Object[]> countByStatusGrouped();

    /**
     * Cuenta solicitudes por prioridad.
     */
    @Query("SELECT m.priority, COUNT(m) FROM MaintenanceRequest m GROUP BY m.priority")
    List<Object[]> countByPriority();

    /**
     * Cuenta solicitudes por tipo.
     */
    @Query("SELECT m.type, COUNT(m) FROM MaintenanceRequest m GROUP BY m.type")
    List<Object[]> countByType();

    /**
     * Suma costos reales de mantenimientos completados.
     */
    @Query("SELECT COALESCE(SUM(m.actualCost), 0) FROM MaintenanceRequest m " +
            "WHERE m.status = 'COMPLETED' AND m.actualCost IS NOT NULL")
    BigDecimal getTotalMaintenanceCosts();

    /**
     * Suma costos por tipo de mantenimiento.
     */
    @Query("SELECT m.type, COALESCE(SUM(m.actualCost), 0) FROM MaintenanceRequest m " +
            "WHERE m.status = 'COMPLETED' AND m.actualCost IS NOT NULL " +
            "GROUP BY m.type")
    List<Object[]> sumCostsByType();

    /**
     * Encuentra solicitudes por técnico con estados activos.
     */
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.assignedTo.id = :userId " +
            "AND m.status IN ('PENDING', 'IN_PROGRESS') " +
            "ORDER BY m.priority DESC, m.dueDate ASC")
    List<MaintenanceRequest> findActiveByTechnician(@Param("userId") Long userId);

    /**
     * Carga de trabajo por técnico (cantidad de tareas activas).
     */
    @Query("SELECT m.assignedTo.id, COUNT(m) FROM MaintenanceRequest m " +
            "WHERE m.status IN ('PENDING', 'IN_PROGRESS') AND m.assignedTo IS NOT NULL " +
            "GROUP BY m.assignedTo.id")
    List<Object[]> getWorkloadByTechnician();
}
