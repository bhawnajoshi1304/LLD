package com.lld.parkinglot.model.factory;

import com.lld.parkinglot.enums.ParkingSpotType;
import com.lld.parkinglot.state.AvailableState;
import com.lld.parkinglot.state.ParkingSpotState;
import com.lld.parkinglot.strategy.AllocationStrategy;

import java.time.LocalDateTime;

public class ParkingSpot {

    private final String spotNumber;
    private Vehicle vehicle;
    private final ParkingSpotType spotType;
    private LocalDateTime occupiedAt;
    private ParkingSpotState state;

    public ParkingSpot(String spotNumber, ParkingSpotType spotType){
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.occupiedAt = null;
        this.state = new AvailableState();
    }

    public boolean canParkVehicle(Vehicle vehicle, AllocationStrategy strategy) {
        return state.canParkVehicle(this, vehicle, strategy);
    }

    public void parkVehicle(Vehicle vehicle) {
        state.parkVehicle(this, vehicle);
    }

    public void exitVehicle(){
        state.exitVehicle(this);
    }

    public boolean isOccupied() {
        return state.isOccupied();
    }

    public String getSpotId() {
        return spotNumber;
    }

    public ParkingSpotType getSpotType() {
        return this.spotType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getOccupiedAt() {
        return occupiedAt;
    }

    public void setOccupiedAt(LocalDateTime occupiedAt) {
        this.occupiedAt = occupiedAt;
    }

    public ParkingSpotState getState() {
        return state;
    }

    public void setState(ParkingSpotState state) {
        this.state = state;
    }
}