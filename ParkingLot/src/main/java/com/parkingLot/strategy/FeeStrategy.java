package main.java.com.parkingLot.strategy;

import main.java.com.parkingLot.enums.ParkingSpotType;

public interface FeeStrategy {
    double calculateFee(ParkingSpotType type, long duration);
}