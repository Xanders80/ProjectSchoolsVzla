package com.school.infra.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.school.academic.validation.ValidationGroups;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "buildings", indexes = {
        @Index(name = "idx_building_name", columnList = "name")
})
@EntityListeners(com.school.core.listener.AuditEntityListener.class)
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del edificio es obligatorio", groups = { ValidationGroups.Create.class,
            ValidationGroups.Update.class })
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres", groups = {
            ValidationGroups.Create.class, ValidationGroups.Update.class })
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "previous_name", length = 100)
    private String previousName;

    @Column(name = "name_changed_at")
    private LocalDateTime nameChangedAt;

    @Size(max = 200, message = "La dirección no puede exceder los 200 caracteres", groups = {
            ValidationGroups.Create.class, ValidationGroups.Update.class })
    @Column(name = "address", length = 200)
    private String address;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    public Building() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String newValue = name != null ? name.trim() : null;
        if (this.name != null && !Objects.equals(this.name, newValue)) {
            this.previousName = this.name;
            this.nameChangedAt = LocalDateTime.now();
        }
        this.name = newValue;
    }

    public String getPreviousName() {
        return previousName;
    }

    public LocalDateTime getNameChangedAt() {
        return nameChangedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Building building))
            return false;
        return Objects.equals(id, building.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Building{id=" + id + ", name='" + name + "'}";
    }
}
