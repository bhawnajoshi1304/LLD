package main.java.com.parkingLot.strategy;

import main.java.com.parkingLot.enums.ParkingSpotType;
import main.java.com.parkingLot.enums.VehicleType;

import java.util.List;

public class FlexibleAllocationStrategy implements AllocationStrategy {

    @Override
    public boolean supportsPark(VehicleType vehicleType, ParkingSpotType spotType) {
        return getPreferredSpotOrder(vehicleType).contains(spotType);
    }

    @Override
    public List<ParkingSpotType> getPreferredSpotOrder(VehicleType vehicleType) {
        switch (vehicleType) {
            case MOTORBIKE:
                return List.of(ParkingSpotType.SMALL, ParkingSpotType.BIG, ParkingSpotType.LARGE);
            case CAR:
                return List.of(ParkingSpotType.BIG, ParkingSpotType.LARGE);
            case TRUCK:
                return List.of(ParkingSpotType.LARGE);
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
        }
    }
}