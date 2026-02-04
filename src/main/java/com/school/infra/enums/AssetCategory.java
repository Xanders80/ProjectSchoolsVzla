package com.school.infra.enums;

/**
 * Categorización estándar de activos escolares.
 * Permite reporting diferenciado, cálculo de depreciación específica por
 * categoría
 * y filtros avanzados en inventario.
 */
public enum AssetCategory {
    FURNITURE("Mobiliario"),
    ELECTRONICS("Electrónica"),
    LABORATORY_EQUIPMENT("Equipo de Laboratorio"),
    SPORTS_EQUIPMENT("Equipo Deportivo"),
    OFFICE_SUPPLIES("Suministros de Oficina"),
    VEHICLE("Vehículos"),
    BUILDING_INFRASTRUCTURE("Infraestructura Edilicia"),
    OTHER("Otro");

    private final String displayName;

    AssetCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
