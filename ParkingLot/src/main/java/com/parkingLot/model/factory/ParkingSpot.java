package main.java.com.parkingLot.model.factory;

import main.java.com.parkingLot.enums.ParkingSpotType;
import main.java.com.parkingLot.model.ParkingLot;

import java.time.LocalDateTime;

public class ParkingSpot {
    private final String spotNumber;
    private boolean occupied;
    private Vehicle vehicle;
    private final ParkingSpotType spotType;
    private LocalDateTime occupiedAt;
    public boolean canParkVehicle(Vehicle vehicle){
        return !occupied && ParkingLot.getInstance().getAllocationStrategy().supportsPark(vehicle.getVehicleType(),this.spotType);
    }
    public ParkingSpot(String spotNumber, ParkingSpotType spotType){
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.occupied = false;
        this.occupiedAt = null;
    }
    public void parkVehicle(Vehicle vehicle) {
        if (occupied) {
            throw new IllegalStateException("Spot is already occupied.");
        }
        if (!canParkVehicle(vehicle)) {
            throw new IllegalArgumentException("This spot is not suitable for" + vehicle.getVehicleType());
        }
        this.vehicle = vehicle;
        this.occupied = true;
        this.occupiedAt = LocalDateTime.now();
    }
    public void exitVehicle(){
            this.occupied = false;
            this.occupiedAt = null;
            this.vehicle = null;
    }
    public boolean isOccupied() {
        return this.occupied;
    }

    public String getSpotId() {
        return spotNumber;
    }

    public ParkingSpotType getSpotType() {
        return this.spotType;
    }

}