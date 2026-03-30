package com.lld.parkinglot.state;

import com.lld.parkinglot.model.factory.ParkingSpot;
import com.lld.parkinglot.model.factory.Vehicle;
import com.lld.parkinglot.strategy.AllocationStrategy;

public class AvailableState implements ParkingSpotState {
    @Override
    public boolean canParkVehicle(ParkingSpot spot, Vehicle vehicle, AllocationStrategy strategy) {
        return strategy.supportsPark(vehicle.getVehicleType(), spot.getSpotType());
    }

    @Override
    public void parkVehicle(ParkingSpot spot, Vehicle vehicle) {
        spot.setVehicle(vehicle);
        spot.setOccupied(true);
        spot.setOccupiedAt(java.time.LocalDateTime.now());
        spot.setState(new OccupiedState());
    }

    @Override
    public void exitVehicle(ParkingSpot spot) {
        throw new IllegalStateException("Cannot exit from an empty spot");
    }

    @Override
    public boolean isOccupied() {
        return false;
    }
}
