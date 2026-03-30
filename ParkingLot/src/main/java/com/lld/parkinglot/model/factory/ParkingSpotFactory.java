package com.lld.parkinglot.model.factory;

import com.lld.parkinglot.enums.ParkingSpotType;

public class ParkingSpotFactory {
    public static ParkingSpot create(String spotId, ParkingSpotType type) {
        return new ParkingSpot(spotId, type);
    }
}
