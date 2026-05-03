package com.lld.parkinglot.model;

import com.lld.parkinglot.model.factory.ParkingSpot;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class ParkingSession {
    private final User user;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public ParkingSession(User user, ParkingSpot spot) {
        this.user = user;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }

    public void endSession() {
        this.exitTime = LocalDateTime.now();
    }

    public long calculateDurationMinutes() {
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        return Duration.between(entryTime, end).toMinutes();
    }
    
    public LocalDateTime getStartTime() {
        return entryTime;
    }
    
    public LocalDateTime getEndTime() {
        return exitTime;
    }
    
    @Override
    public String toString() {
        return
                "model.factory.VehicleFactory.Vehicle=" + user.getVehicle().getNumberPlate() +
                ", Spot=" + spot.getSpotId() +
                ", Duration=" + calculateDurationMinutes() + " mins" ;
    }
}
