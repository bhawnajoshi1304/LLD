package com.lld.parkinglot.model.factory;

import com.lld.parkinglot.enums.ParkingSpotType;
import com.lld.parkinglot.state.AvailableState;
import com.lld.parkinglot.state.ParkingSpotState;
import com.lld.parkinglot.strategy.AllocationStrategy;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ParkingSpot {

    @Getter
    private final String spotNumber;
    @Getter
    @Setter
    private Vehicle vehicle;
    @Getter
    private final ParkingSpotType spotType;
    @Getter
    @Setter
    private LocalDateTime occupiedAt;
    @Getter
    @Setter
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
    
    public void setOccupied(boolean occupied) {
        // Occupancy is managed through state pattern
        // This method exists for state class compatibility
    }
    
    public String getSpotId() {
        return spotNumber;
    }
}