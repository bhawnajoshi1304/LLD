package com.lld.parkinglot.state;

import com.lld.parkinglot.model.factory.ParkingSpot;
import com.lld.parkinglot.model.factory.Vehicle;
import com.lld.parkinglot.strategy.AllocationStrategy;

public class OutOfServiceState implements ParkingSpotState {
    @Override
    public boolean canParkVehicle(ParkingSpot spot, Vehicle vehicle, AllocationStrategy strategy) {
        return false;
    }

    @Override
    public void parkVehicle(ParkingSpot spot, Vehicle vehicle) {
        throw new IllegalStateException("Spot is out of service");
    }

    @Override
    public void exitVehicle(ParkingSpot spot) {
        throw new IllegalStateException("Cannot exit from out of service spot");
    }

    @Override
    public boolean isOccupied() {
        return false;
    }
}
