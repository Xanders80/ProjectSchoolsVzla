package com.school.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.infra.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByBuildingId(Long buildingId);
}
