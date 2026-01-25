package com.school.infra.entity;

import java.time.LocalDateTime;

import com.school.core.entity.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.school.core.listener.AuditEntityListener;

@Entity
@Table(name = "maintenance_requests")
@EntityListeners(AuditEntityListener.class)
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La ubicación es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id")
    private User requestedBy;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String description;

    private String status; // PENDING, IN_PROGRESS, COMPLETED
    private LocalDateTime requestDate = LocalDateTime.now();
    private LocalDateTime completionDate;

    public MaintenanceRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }
}
