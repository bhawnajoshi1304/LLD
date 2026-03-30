package main.java.com.lld.parkinglot.model.factory;

import main.java.com.lld.parkinglot.enums.ParkingSpotType;

public class ParkingSpotFactory {
    public static ParkingSpot create(String spotId, ParkingSpotType type) {
        return new ParkingSpot(spotId, type);
    }
}
