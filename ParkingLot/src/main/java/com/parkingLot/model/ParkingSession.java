package main.java.com.parkingLot.model;

import main.java.com.parkingLot.model.factory.ParkingSpot;

import java.time.Duration;
import java.time.LocalDateTime;

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
        this.spot.exitVehicle();
    }

    public long calculateDurationMinutes() {
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        return Duration.between(entryTime, end).toMinutes();
    }
    public User getUser() { return user; }
    public ParkingSpot getSpot() { return spot; }

    @Override
    public String toString() {
        return
                "model.factory.VehicleFactory.Vehicle=" + user.getVehicle().getNumberPlate() +
                ", Spot=" + spot.getSpotId() +
                ", Duration=" + calculateDurationMinutes() + " mins" ;
    }
}
