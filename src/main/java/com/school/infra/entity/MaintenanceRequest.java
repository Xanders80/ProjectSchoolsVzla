package com.school.infra.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.school.core.entity.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.school.core.listener.AuditEntityListener;
import com.school.infra.enums.MaintenanceStatus;
import com.school.infra.enums.MaintenancePriority;
import com.school.infra.enums.MaintenanceType;

/**
 * Entidad que representa una solicitud de mantenimiento.
 * Puede estar asociada a un activo específico o a una ubicación general.
 * Incluye workflow de estados, priorización con SLA y tracking de costos.
 */
@Entity
@Table(name = "maintenance_requests", indexes = {
        @Index(name = "idx_maintenance_status", columnList = "status"),
        @Index(name = "idx_maintenance_priority", columnList = "priority"),
        @Index(name = "idx_maintenance_due_date", columnList = "dueDate"),
        @Index(name = "idx_maintenance_asset", columnList = "asset_id")
})
@EntityListeners(AuditEntityListener.class)
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Activo específico al que aplica el mantenimiento (opcional).
     * Si es null, el mantenimiento es de infraestructura general (room).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    /**
     * Ubicación del mantenimiento.
     * Puede ser la ubicación del activo o una ubicación general.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id")
    private User requestedBy;

    /**
     * Técnico asignado para realizar el mantenimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MaintenanceStatus status = MaintenanceStatus.PENDING;

    @NotNull(message = "La prioridad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MaintenancePriority priority = MaintenancePriority.MEDIUM;

    @NotNull(message = "El tipo de mantenimiento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MaintenanceType type;

    @Column(nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    /**
     * Fecha programada para realizar el mantenimiento (opcional).
     */
    private LocalDate scheduledDate;

    /**
     * Fecha límite calculada según SLA de la prioridad.
     * Se calcula automáticamente en @PrePersist.
     */
    private LocalDate dueDate;

    private LocalDateTime completionDate;

    /**
     * Descripción de la resolución/trabajo realizado.
     * Obligatorio al completar el mantenimiento.
     */
    @Size(max = 2000)
    @Column(length = 2000)
    private String resolution;

    /**
     * Costo estimado del mantenimiento.
     */
    @PositiveOrZero(message = "El costo estimado no puede ser negativo")
    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    /**
     * Costo real del mantenimiento una vez completado.
     */
    @PositiveOrZero(message = "El costo real no puede ser negativo")
    @Column(precision = 10, scale = 2)
    private BigDecimal actualCost;

    // Constructores
    public MaintenanceRequest() {
    }

    public MaintenanceRequest(String description, MaintenanceType type) {
        this.description = description;
        this.type = type;
        this.priority = type.getSuggestedPriority(); // Prioridad sugerida según tipo
    }

    // Métodos de ciclo de vida JPA

    @PrePersist
    private void onCreate() {
        calculateDueDate();
    }

    @PreUpdate
    private void onUpdate() {
        // Recalcular due date si cambia la prioridad y aún está pendiente
        if (status == MaintenanceStatus.PENDING && dueDate == null) {
            calculateDueDate();
        }
    }

    // Métodos de negocio

    /**
     * Calcula la fecha límite (due date) basándose en la prioridad y SLA.
     */
    private void calculateDueDate() {
        if (priority != null && requestDate != null && dueDate == null) {
            LocalDate requestLocalDate = requestDate.toLocalDate();
            this.dueDate = requestLocalDate.plusDays(priority.getSlaDays());
        }
    }

    /**
     * Cambia el estado validando transiciones permitidas según workflow.
     *
     * @param newStatus Estado objetivo
     * @throws IllegalStateException si la transición no es válida
     */
    public void setStatus(MaintenanceStatus newStatus) {
        if (this.status != null && !this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Transición inválida: no se puede cambiar de %s a %s",
                            this.status.getDisplayName(),
                            newStatus.getDisplayName()));
        }
        this.status = newStatus;
    }

    /**
     * Verifica si la solicitud está vencida según SLA.
     */
    public boolean isOverdue() {
        if (status.isFinalState()) {
            return false; // Las completadas/canceladas no están vencidas
        }
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Calcula días restantes hasta el vencimiento (negativo si ya venció).
     */
    public long getDaysUntilDue() {
        if (dueDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * Verifica si el mantenimiento está próximo a vencer (menos de 2 días).
     */
    public boolean isNearDueDate() {
        if (status.isFinalState()) {
            return false;
        }
        long daysLeft = getDaysUntilDue();
        return daysLeft >= 0 && daysLeft <= 2;
    }

    /**
     * Valida que la solicitud tenga al menos un activo o una ubicación.
     */
    @AssertTrue(message = "La solicitud debe tener un activo o una ubicación asociada")
    private boolean isAssetOrRoomPresent() {
        return asset != null || room != null;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MaintenanceStatus getStatus() {
        return status;
    }

    // setStatus con validación ya definido arriba

    public MaintenancePriority getPriority() {
        return priority;
    }

    public void setPriority(MaintenancePriority priority) {
        this.priority = priority;
        // Recalcular due date si cambia la prioridad
        if (this.dueDate == null) {
            calculateDueDate();
        }
    }

    public MaintenanceType getType() {
        return type;
    }

    public void setType(MaintenanceType type) {
        this.type = type;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }
}
