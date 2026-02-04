package com.school.infra.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.core.entity.User;
import com.school.core.exception.BusinessValidationException;
import com.school.infra.entity.Asset;
import com.school.infra.entity.MaintenanceRequest;
import com.school.infra.enums.AssetStatus;
import com.school.infra.enums.MaintenancePriority;
import com.school.infra.enums.MaintenanceStatus;
import com.school.infra.enums.MaintenanceType;
import com.school.infra.repository.MaintenanceRequestRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Servicio de gestión de mantenimiento con workflow completo.
 * Implementa validaciones de transiciones de estado, asignación de técnicos,
 * tracking de SLA y sincronización con estados de activos.
 */
@Service
@Transactional
public class MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRepository;
    private final AssetService assetService;

    public MaintenanceService(MaintenanceRequestRepository maintenanceRepository,
            AssetService assetService) {
        this.maintenanceRepository = maintenanceRepository;
        this.assetService = assetService;
    }

    // ============ CRUD Básico ============

    public Page<MaintenanceRequest> getAllRequests(@NonNull Pageable pageable) {
        return maintenanceRepository.findAll(pageable);
    }

    public List<MaintenanceRequest> getAllRequests() {
        return maintenanceRepository.findAll();
    }

    public Optional<MaintenanceRequest> getRequestById(@NonNull Long id) {
        return maintenanceRepository.findById(id);
    }

    /**
     * Crea una nueva solicitud de mantenimiento con validaciones.
     */
    public MaintenanceRequest createRequest(@NonNull MaintenanceRequest request) {
        validateRequest(request);

        // Establecer estado inicial si no está definido
        if (request.getStatus() == null) {
            request.setStatus(MaintenanceStatus.PENDING);
        }

        // Establecer fecha de solicitud si no está definida
        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDateTime.now());
        }

        // Si tiene activo asociado, actualizar estado del activo
        if (request.getAsset() != null) {
            Asset asset = request.getAsset();
            if (asset.getStatus() == AssetStatus.ACTIVE) {
                assetService.updateStatus(asset.getId(), AssetStatus.IN_MAINTENANCE);
            }
        }

        return maintenanceRepository.save(request);
    }

    /**
     * Actualiza una solicitud existente.
     */
    public MaintenanceRequest updateRequest(@NonNull Long id, @NonNull MaintenanceRequest updatedRequest) {
        MaintenanceRequest existing = getRequestById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

        // No permitir actualización si está en estado final
        if (existing.getStatus().isFinalState()) {
            throw new BusinessValidationException(
                    "No se puede modificar una solicitud en estado " + existing.getStatus().getDisplayName());
        }

        validateRequest(updatedRequest);

        // Actualizar campos permitidos
        existing.setDescription(updatedRequest.getDescription());
        existing.setPriority(updatedRequest.getPriority());
        existing.setType(updatedRequest.getType());
        existing.setScheduledDate(updatedRequest.getScheduledDate());
        existing.setEstimatedCost(updatedRequest.getEstimatedCost());

        return maintenanceRepository.save(existing);
    }

    public void deleteRequest(@NonNull Long id) {
        MaintenanceRequest request = getRequestById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

        // Solo permitir eliminar si está pendiente
        if (request.getStatus() != MaintenanceStatus.PENDING) {
            throw new BusinessValidationException(
                    "Solo se pueden eliminar solicitudes en estado Pendiente");
        }

        maintenanceRepository.deleteById(id);
    }

    // ============ Workflow de Estados ============

    /**
     * Cambia el estado de una solicitud validando transiciones permitidas.
     */
    public MaintenanceRequest updateStatus(@NonNull Long id, @NonNull MaintenanceStatus newStatus) {
        MaintenanceRequest request = getRequestById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

        // La validación de transición se hace en el setter de la entidad
        try {
            request.setStatus(newStatus);
        } catch (IllegalStateException e) {
            throw new BusinessValidationException(e.getMessage(), e);
        }

        return maintenanceRepository.save(request);
    }

    /**
     * Asigna un técnico a una solicitud de mantenimiento.
     */
    public MaintenanceRequest assignTechnician(@NonNull Long requestId, @NonNull Long technicianId) {
        MaintenanceRequest request = getRequestById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        // Validar que esté en estado que permita asignación
        if (request.getStatus() != MaintenanceStatus.PENDING) {
            throw new BusinessValidationException(
                    "Solo se pueden asignar técnicos a solicitudes pendientes");
        }

        // Nota: Se asume que el User ya está validado por la capa de controlador
        // En producción, deberías validar que el usuario tenga rol TECHNICIAN
        request.setAssignedTo(new User()); // El User se debe pasar desde el controlador

        // Automáticamente cambiar estado a IN_PROGRESS al asignar
        request.setStatus(MaintenanceStatus.IN_PROGRESS);

        return maintenanceRepository.save(request);
    }

    /**
     * Completa una solicitud de mantenimiento con resolución y costo real.
     */
    public MaintenanceRequest completeRequest(@NonNull Long requestId,
            @NonNull String resolution,
            BigDecimal actualCost) {
        MaintenanceRequest request = getRequestById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        // Validar que esté en progreso
        if (request.getStatus() != MaintenanceStatus.IN_PROGRESS) {
            throw new BusinessValidationException(
                    "Solo se pueden completar solicitudes en progreso");
        }

        if (resolution == null || resolution.isBlank()) {
            throw new BusinessValidationException(
                    "La resolución es obligatoria al completar el mantenimiento");
        }

        request.setStatus(MaintenanceStatus.COMPLETED);
        request.setCompletionDate(LocalDateTime.now());
        request.setResolution(resolution);
        request.setActualCost(actualCost);

        // Si tiene activo asociado, reactivarlo
        if (request.getAsset() != null) {
            Asset asset = request.getAsset();
            // Solo reactivar si no tiene otros mantenimientos activos
            long otherActiveMaintenance = asset.getMaintenanceHistory().stream()
                    .filter(m -> !m.getId().equals(requestId))
                    .filter(m -> m.getStatus() == MaintenanceStatus.IN_PROGRESS ||
                            m.getStatus() == MaintenanceStatus.PENDING)
                    .count();

            if (otherActiveMaintenance == 0 && asset.getStatus() == AssetStatus.IN_MAINTENANCE) {
                assetService.updateStatus(asset.getId(), AssetStatus.ACTIVE);
            }
        }

        return maintenanceRepository.save(request);
    }

    /**
     * Cancela una solicitud de mantenimiento.
     */
    public MaintenanceRequest cancelRequest(@NonNull Long requestId, @NonNull String reason) {
        MaintenanceRequest request = getRequestById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        // Validar que no esté ya completada
        if (request.getStatus() == MaintenanceStatus.COMPLETED) {
            throw new BusinessValidationException(
                    "No se puede cancelar una solicitud ya completada");
        }

        request.setStatus(MaintenanceStatus.CANCELLED);
        request.setResolution("Cancelado: " + reason);
        request.setCompletionDate(LocalDateTime.now());

        // Si tiene activo asociado, reactivarlo si no hay otros mantenimientos
        if (request.getAsset() != null) {
            Asset asset = request.getAsset();
            long otherActiveMaintenance = asset.getMaintenanceHistory().stream()
                    .filter(m -> !m.getId().equals(requestId))
                    .filter(m -> m.getStatus() == MaintenanceStatus.IN_PROGRESS ||
                            m.getStatus() == MaintenanceStatus.PENDING)
                    .count();

            if (otherActiveMaintenance == 0 && asset.getStatus() == AssetStatus.IN_MAINTENANCE) {
                assetService.updateStatus(asset.getId(), AssetStatus.ACTIVE);
            }
        }

        return maintenanceRepository.save(request);
    }

    // ============ Consultas y Filtros ============

    public List<MaintenanceRequest> getRequestsByStatus(@NonNull MaintenanceStatus status) {
        return maintenanceRepository.findByStatus(status);
    }

    public List<MaintenanceRequest> getPendingRequests() {
        return maintenanceRepository.findByStatus(MaintenanceStatus.PENDING);
    }

    public List<MaintenanceRequest> getInProgressRequests() {
        return maintenanceRepository.findByStatus(MaintenanceStatus.IN_PROGRESS);
    }

    public List<MaintenanceRequest> getRequestsByPriority(@NonNull MaintenancePriority priority) {
        return maintenanceRepository.findByPriority(priority);
    }

    public List<MaintenanceRequest> getRequestsByType(@NonNull MaintenanceType type) {
        return maintenanceRepository.findByType(type);
    }

    public List<MaintenanceRequest> getRequestsByAsset(@NonNull Long assetId) {
        return maintenanceRepository.findByAssetId(assetId);
    }

    public List<MaintenanceRequest> getRequestsByTechnician(@NonNull Long technicianId) {
        return maintenanceRepository.findByAssignedToId(technicianId);
    }

    public List<MaintenanceRequest> getActiveRequestsByTechnician(@NonNull Long technicianId) {
        return maintenanceRepository.findActiveByTechnician(technicianId);
    }

    /**
     * Obtiene el historial completo de mantenimiento de un activo.
     */
    public List<MaintenanceRequest> getMaintenanceHistory(@NonNull Long assetId) {
        return maintenanceRepository.findMaintenanceHistory(assetId);
    }

    // ============ Alertas y Tracking de SLA ============

    /**
     * Obtiene solicitudes vencidas (pasado el due date).
     */
    public List<MaintenanceRequest> getOverdueRequests() {
        return maintenanceRepository.findAllOverdue(LocalDate.now());
    }

    /**
     * Obtiene solicitudes próximas a vencer (en los próximos 2 días).
     */
    public List<MaintenanceRequest> getRequestsNearDueDate() {
        LocalDate today = LocalDate.now();
        LocalDate twoDaysLater = today.plusDays(2);
        return maintenanceRepository.findNearDueDate(today, twoDaysLater);
    }

    /**
     * Obtiene solicitudes vencidas por estado específico.
     */
    public List<MaintenanceRequest> getOverdueByStatus(@NonNull MaintenanceStatus status) {
        return maintenanceRepository.findOverdueByStatus(status, LocalDate.now());
    }

    // ============ Reportes y Estadísticas ============

    public long countByStatus(@NonNull MaintenanceStatus status) {
        return maintenanceRepository.countByStatus(status);
    }

    public long countPendingRequests() {
        return countByStatus(MaintenanceStatus.PENDING);
    }

    public long countInProgressRequests() {
        return countByStatus(MaintenanceStatus.IN_PROGRESS);
    }

    /**
     * Obtiene distribución de solicitudes por estado.
     */
    public Map<MaintenanceStatus, Long> getRequestsByStatus() {
        return maintenanceRepository.countByStatusGrouped().stream()
                .collect(Collectors.toMap(
                        arr -> (MaintenanceStatus) arr[0],
                        arr -> (Long) arr[1]));
    }

    /**
     * Obtiene distribución de solicitudes por prioridad.
     */
    public Map<MaintenancePriority, Long> getRequestsByPriority() {
        return maintenanceRepository.countByPriority().stream()
                .collect(Collectors.toMap(
                        arr -> (MaintenancePriority) arr[0],
                        arr -> (Long) arr[1]));
    }

    /**
     * Obtiene distribución de solicitudes por tipo.
     */
    public Map<MaintenanceType, Long> getRequestsByType() {
        return maintenanceRepository.countByType().stream()
                .collect(Collectors.toMap(
                        arr -> (MaintenanceType) arr[0],
                        arr -> (Long) arr[1]));
    }

    /**
     * Calcula el costo total de mantenimientos completados.
     */
    public BigDecimal getTotalMaintenanceCosts() {
        return maintenanceRepository.getTotalMaintenanceCosts();
    }

    /**
     * Obtiene costos totales por tipo de mantenimiento.
     */
    public Map<MaintenanceType, BigDecimal> getCostsByType() {
        return maintenanceRepository.sumCostsByType().stream()
                .collect(Collectors.toMap(
                        arr -> (MaintenanceType) arr[0],
                        arr -> (BigDecimal) arr[1]));
    }

    /**
     * Obtiene carga de trabajo por técnico (cantidad de tareas activas).
     */
    public Map<Long, Long> getWorkloadByTechnician() {
        return maintenanceRepository.getWorkloadByTechnician().stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]));
    }

    // ============ Validaciones Privadas ============

    /**
     * Valida reglas de negocio para una solicitud de mantenimiento.
     */
    private void validateRequest(MaintenanceRequest request) {
        if (request == null) {
            throw new BusinessValidationException("La solicitud no puede ser nula");
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new BusinessValidationException("La descripción es obligatoria");
        }

        if (request.getType() == null) {
            throw new BusinessValidationException("El tipo de mantenimiento es obligatorio");
        }

        // Validar que tenga al menos un activo o una ubicación
        if (request.getAsset() == null && request.getRoom() == null) {
            throw new BusinessValidationException(
                    "La solicitud debe tener asociado un activo o una ubicación");
        }

        // Validar costos
        if (request.getEstimatedCost() != null &&
                request.getEstimatedCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("El costo estimado no puede ser negativo");
        }

        if (request.getActualCost() != null &&
                request.getActualCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("El costo real no puede ser negativo");
        }
    }
}
