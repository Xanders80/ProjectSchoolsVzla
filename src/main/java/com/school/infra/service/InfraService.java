package com.school.infra.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.infra.entity.Building;
import com.school.infra.entity.Room;
import com.school.infra.repository.BuildingRepository;
import com.school.infra.repository.RoomRepository;

@Service
@Transactional
public class InfraService {

    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;

    public InfraService(BuildingRepository buildingRepository, RoomRepository roomRepository) {
        this.buildingRepository = buildingRepository;
        this.roomRepository = roomRepository;
    }

    // Building Ops
    public List<Building> getAllBuildings() {
        return buildingRepository.findByDeletedFalse();
    }

    public org.springframework.data.domain.Page<Building> getAllBuildings(
            @NonNull org.springframework.data.domain.Pageable pageable) {
        return buildingRepository.findByDeletedFalse(pageable);
    }

    public Building saveBuilding(@NonNull Building building) {
        return buildingRepository.save(building);
    }

    public void deleteBuilding(@NonNull Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Edificio no encontrado"));

        building.setDeleted(true);
        building.setDeletedAt(java.time.LocalDateTime.now());
        building.setDeletedBy(getCurrentUser());

        buildingRepository.save(building);
    }

    public Optional<Building> getBuildingById(@NonNull Long id) {
        return buildingRepository.findById(id);
    }

    // Room Ops
    public List<Room> getAllRooms() {
        return roomRepository.findByDeletedFalse();
    }

    public org.springframework.data.domain.Page<Room> getAllRooms(
            @NonNull org.springframework.data.domain.Pageable pageable) {
        return roomRepository.findByDeletedFalse(pageable);
    }

    public List<Room> getRoomsByBuilding(Long buildingId) {
        return roomRepository.findByBuildingId(buildingId);
    }

    public Optional<Room> getRoomById(@NonNull Long id) {
        return roomRepository.findById(id);
    }

    public Room saveRoom(@NonNull Room room) {
        return roomRepository.save(room);
    }

    public void deleteRoom(@NonNull Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aula no encontrada"));

        room.setDeleted(true);
        room.setDeletedAt(java.time.LocalDateTime.now());
        room.setDeletedBy(getCurrentUser());

        roomRepository.save(room);
    }

    @NonNull
    private String getCurrentUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null) {
            String name = auth.getName();
            if (name != null) {
                return name;
            }
        }
        return "system";
    }
}
