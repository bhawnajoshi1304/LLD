package main.java.com.parkingLot.strategy;

import main.java.com.parkingLot.enums.ParkingSpotType;
import main.java.com.parkingLot.enums.VehicleType;
import java.util.List;

public interface AllocationStrategy {
    boolean supportsPark(VehicleType vehicleType, ParkingSpotType spotType);
    List<ParkingSpotType> getPreferredSpotOrder(VehicleType vehicleType);
}