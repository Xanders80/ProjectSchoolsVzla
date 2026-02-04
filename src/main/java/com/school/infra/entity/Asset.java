package com.school.infra.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.school.core.listener.AuditEntityListener;
import com.school.infra.enums.AssetCategory;
import com.school.infra.enums.AssetCondition;
import com.school.infra.enums.AssetStatus;

/**
 * Entidad que representa un activo físico del establecimiento escolar.
 * Incluye tracking completo de ciclo de vida, depreciación y historial de
 * mantenimiento.
 */
@Entity
@Table(name = "assets", indexes = {
        @Index(name = "idx_asset_serial", columnList = "serialNumber"),
        @Index(name = "idx_asset_category_status", columnList = "category, status")
})
@EntityListeners(AuditEntityListener.class)
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Size(max = 100)
    @Column(unique = true)
    private String serialNumber;

    @NotNull(message = "La categoría es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AssetCategory category;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssetStatus status = AssetStatus.ACTIVE;

    @NotNull(message = "La condición es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetCondition condition = AssetCondition.GOOD;

    @PastOrPresent(message = "La fecha de compra no puede ser futura")
    private LocalDate purchaseDate;

    @Positive(message = "El valor de compra debe ser positivo")
    @Column(precision = 10, scale = 2)
    private BigDecimal purchaseValue;

    /**
     * Tasa de depreciación anual como porcentaje (ej: 0.15 = 15% anual).
     * Si es null, no se aplica depreciación.
     */
    @DecimalMin(value = "0.0", message = "La tasa de depreciación no puede ser negativa")
    @DecimalMax(value = "1.0", message = "La tasa de depreciación no puede exceder 100%")
    @Column(precision = 5, scale = 4)
    private BigDecimal depreciationRate;

    @Size(max = 100)
    private String manufacturer;

    @Size(max = 100)
    private String model;

    /**
     * Duración de la garantía en meses desde la fecha de compra.
     */
    @Min(value = 0, message = "Los meses de garantía no pueden ser negativos")
    private Integer warrantyMonths;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room; // Ubicación actual

    /**
     * Relación bidireccional con historial de mantenimiento.
     * Permite consultar fácilmente todos los mantenimientos de un activo.
     */
    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<MaintenanceRequest> maintenanceHistory = new ArrayList<>();

    // Constructores
    public Asset() {
    }

    public Asset(String name, AssetCategory category) {
        this.name = name;
        this.category = category;
    }

    // Métodos de negocio

    /**
     * Calcula la fecha de expiración de la garantía.
     * 
     * @return Fecha de expiración o null si no hay garantía
     */
    public LocalDate getWarrantyExpiry() {
        if (purchaseDate != null && warrantyMonths != null && warrantyMonths > 0) {
            return purchaseDate.plusMonths(warrantyMonths);
        }
        return null;
    }

    /**
     * Verifica si el activo está actualmente bajo garantía.
     */
    public boolean isUnderWarranty() {
        LocalDate expiryDate = getWarrantyExpiry();
        return expiryDate != null && LocalDate.now().isBefore(expiryDate);
    }

    /**
     * Calcula el valor actual del activo considerando depreciación lineal.
     *
     * @param asOf Fecha a la cual calcular el valor (null = hoy)
     * @return Valor depreciado o null si no hay datos suficientes
     */
    public BigDecimal calculateCurrentValue(LocalDate asOf) {
        if (purchaseValue == null || purchaseDate == null) {
            return null;
        }

        LocalDate calculationDate = asOf != null ? asOf : LocalDate.now();

        // Sin depreciación configurada, retorna valor de compra ajustado por condición
        if (depreciationRate == null || depreciationRate.compareTo(BigDecimal.ZERO) == 0) {
            return purchaseValue.multiply(BigDecimal.valueOf(condition.getConditionFactor()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Calcular años transcurridos
        long daysBetween = ChronoUnit.DAYS.between(purchaseDate, calculationDate);
        if (daysBetween < 0) {
            return purchaseValue; // Fecha anterior a la compra
        }

        BigDecimal yearsPassed = BigDecimal.valueOf(daysBetween)
                .divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);

        // Depreciación lineal: Valor = ValorCompra * (1 - tasa * años)
        BigDecimal depreciationFactor = BigDecimal.ONE.subtract(
                depreciationRate.multiply(yearsPassed));

        // No permitir valores negativos
        if (depreciationFactor.compareTo(BigDecimal.ZERO) < 0) {
            depreciationFactor = BigDecimal.ZERO;
        }

        BigDecimal depreciatedValue = purchaseValue.multiply(depreciationFactor);

        // Aplicar factor de condición
        BigDecimal finalValue = depreciatedValue.multiply(
                BigDecimal.valueOf(condition.getConditionFactor()));

        return finalValue.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Determina si el activo necesita mantenimiento basándose en su condición.
     */
    public boolean needsMaintenance() {
        return condition.requiresMaintenance();
    }

    /**
     * Cuenta cuántos mantenimientos tiene el activo en estado específico.
     */
    public long countMaintenanceByStatus(com.school.infra.enums.MaintenanceStatus status) {
        return maintenanceHistory.stream()
                .filter(m -> m.getStatus() == status)
                .count();
    }

    /**
     * Verifica si tiene algún mantenimiento activo.
     */
    public boolean hasActiveMaintenante() {
        return countMaintenanceByStatus(com.school.infra.enums.MaintenanceStatus.IN_PROGRESS) > 0 ||
                countMaintenanceByStatus(com.school.infra.enums.MaintenanceStatus.PENDING) > 0;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public AssetCondition getCondition() {
        return condition;
    }

    public void setCondition(AssetCondition condition) {
        this.condition = condition;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getPurchaseValue() {
        return purchaseValue;
    }

    public void setPurchaseValue(BigDecimal purchaseValue) {
        this.purchaseValue = purchaseValue;
    }

    public BigDecimal getDepreciationRate() {
        return depreciationRate;
    }

    public void setDepreciationRate(BigDecimal depreciationRate) {
        this.depreciationRate = depreciationRate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<MaintenanceRequest> getMaintenanceHistory() {
        return maintenanceHistory;
    }

    public void setMaintenanceHistory(List<MaintenanceRequest> maintenanceHistory) {
        this.maintenanceHistory = maintenanceHistory;
    }

    /**
     * Helper para agregar mantenimiento manteniendo sincronizada la relación
     * bidireccional.
     */
    public void addMaintenance(MaintenanceRequest maintenance) {
        maintenanceHistory.add(maintenance);
        maintenance.setAsset(this);
    }

    /**
     * Helper para remover mantenimiento manteniendo sincronizada la relación
     * bidireccional.
     */
    public void removeMaintenance(MaintenanceRequest maintenance) {
        maintenanceHistory.remove(maintenance);
        maintenance.setAsset(null);
    }
}
