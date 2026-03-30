package com.lld.parkinglot.state;

import com.lld.parkinglot.model.factory.ParkingSpot;
import com.lld.parkinglot.model.factory.Vehicle;
import com.lld.parkinglot.strategy.AllocationStrategy;

public interface ParkingSpotState {
    boolean canParkVehicle(ParkingSpot spot, Vehicle vehicle, AllocationStrategy strategy);
    void parkVehicle(ParkingSpot spot, Vehicle vehicle);
    void exitVehicle(ParkingSpot spot);
    boolean isOccupied();
}
