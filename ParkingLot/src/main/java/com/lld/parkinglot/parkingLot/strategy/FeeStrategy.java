package main.java.com.lld.parkinglot.strategy;

import main.java.com.lld.parkinglot.enums.ParkingSpotType;

public interface FeeStrategy {
    double calculateFee(ParkingSpotType type, long duration);
}