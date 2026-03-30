package com.lld.parkinglot.state;

import com.lld.parkinglot.model.factory.ParkingSpot;
import com.lld.parkinglot.model.factory.Vehicle;
import com.lld.parkinglot.strategy.AllocationStrategy;

public class OccupiedState implements ParkingSpotState {
    @Override
    public boolean canParkVehicle(ParkingSpot spot, Vehicle vehicle, AllocationStrategy strategy) {
        return false;
    }

    @Override
    public void parkVehicle(ParkingSpot spot, Vehicle vehicle) {
        throw new IllegalStateException("Spot is already occupied");
    }

    @Override
    public void exitVehicle(ParkingSpot spot) {
        spot.setVehicle(null);
        spot.setOccupied(false);
        spot.setOccupiedAt(null);
        spot.setState(new AvailableState());
    }

    @Override
    public boolean isOccupied() {
        return true;
    }
}
