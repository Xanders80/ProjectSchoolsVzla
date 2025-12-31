package com.school.infra.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.school.academic.validation.ValidationGroups;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "rooms", indexes = {
        @Index(name = "idx_room_number", columnList = "roomNumber"),
        @Index(name = "idx_room_building", columnList = "building_id"),
        @Index(name = "idx_room_type", columnList = "type")
})
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de aula es obligatorio", groups = { ValidationGroups.Create.class,
            ValidationGroups.Update.class })
    @Size(min = 1, max = 20, message = "El número de aula debe tener entre 1 y 20 caracteres", groups = {
            ValidationGroups.Create.class, ValidationGroups.Update.class })
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "El número de aula solo puede contener letras mayúsculas, números y guiones", groups = {
            ValidationGroups.Create.class, ValidationGroups.Update.class })
    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Column(name = "previous_room_number", length = 20)
    private String previousRoomNumber;

    @Column(name = "room_number_changed_at")
    private LocalDateTime roomNumberChangedAt;

    @Min(value = 1, message = "La capacidad mínima es 1", groups = { ValidationGroups.Create.class,
            ValidationGroups.Update.class })
    @Max(value = 500, message = "La capacidad máxima es 500", groups = { ValidationGroups.Create.class,
            ValidationGroups.Update.class })
    @Column(name = "capacity")
    private Integer capacity;

    @Pattern(regexp = "^(CLASSROOM|LAB|OFFICE|AUDITORIUM|LIBRARY)$", message = "El tipo debe ser: CLASSROOM, LAB, OFFICE, AUDITORIUM o LIBRARY", groups = {
            ValidationGroups.Create.class, ValidationGroups.Update.class })
    @Column(name = "type", length = 100)
    private String type;

    @NotNull(message = "El edificio es obligatorio", groups = ValidationGroups.Create.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", foreignKey = @ForeignKey(name = "fk_room_building"))
    private Building building;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    public Room() {
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        String newValue = roomNumber != null ? roomNumber.trim().toUpperCase() : null;
        if (this.roomNumber != null && !Objects.equals(this.roomNumber, newValue)) {
            this.previousRoomNumber = this.roomNumber;
            this.roomNumberChangedAt = LocalDateTime.now();
        }
        this.roomNumber = newValue;
    }

    public String getPreviousRoomNumber() {
        return previousRoomNumber;
    }

    public LocalDateTime getRoomNumberChangedAt() {
        return roomNumberChangedAt;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type != null ? type.trim().toUpperCase() : null;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Room room))
            return false;
        return Objects.equals(id, room.id) && Objects.equals(roomNumber, room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomNumber);
    }

    @Override
    public String toString() {
        return "Room{id=" + id + ", roomNumber='" + roomNumber + "', type='" + type + "'}";
    }
}
