package com.school.infra.dto;

public class LabStatisticsDTO {
    private String roomNumber;
    private Long totalReservations;
    private Long approvedReservations;
    private Long rejectedReservations;
    private Long pendingReservations;
    private Double occupancyRate;
    private Long totalHoursReserved;

    public LabStatisticsDTO() {
    }

    public LabStatisticsDTO(String roomNumber, Long totalReservations, Long approvedReservations,
            Long rejectedReservations, Long pendingReservations, Double occupancyRate, Long totalHoursReserved) {
        this.roomNumber = roomNumber;
        this.totalReservations = totalReservations;
        this.approvedReservations = approvedReservations;
        this.rejectedReservations = rejectedReservations;
        this.pendingReservations = pendingReservations;
        this.occupancyRate = occupancyRate;
        this.totalHoursReserved = totalHoursReserved;
    }

    // Getters and Setters
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(Long totalReservations) {
        this.totalReservations = totalReservations;
    }

    public Long getApprovedReservations() {
        return approvedReservations;
    }

    public void setApprovedReservations(Long approvedReservations) {
        this.approvedReservations = approvedReservations;
    }

    public Long getRejectedReservations() {
        return rejectedReservations;
    }

    public void setRejectedReservations(Long rejectedReservations) {
        this.rejectedReservations = rejectedReservations;
    }

    public Long getPendingReservations() {
        return pendingReservations;
    }

    public void setPendingReservations(Long pendingReservations) {
        this.pendingReservations = pendingReservations;
    }

    public Double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(Double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public Long getTotalHoursReserved() {
        return totalHoursReserved;
    }

    public void setTotalHoursReserved(Long totalHoursReserved) {
        this.totalHoursReserved = totalHoursReserved;
    }
}
