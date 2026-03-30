package com.lld.parkinglot.strategy;

import com.lld.parkinglot.enums.ParkingSpotType;

public interface FeeStrategy {
    double calculateFee(ParkingSpotType type, long duration);
}