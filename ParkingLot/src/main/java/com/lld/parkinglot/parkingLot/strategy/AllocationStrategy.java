package main.java.com.lld.parkinglot.strategy;

import main.java.com.lld.parkinglot.enums.ParkingSpotType;
import main.java.com.lld.parkinglot.enums.VehicleType;
import java.util.List;

public interface AllocationStrategy {
    boolean supportsPark(VehicleType vehicleType, ParkingSpotType spotType);
    List<ParkingSpotType> getPreferredSpotOrder(VehicleType vehicleType);
}