package main.java.com.parkingLot.model.factory;

import main.java.com.parkingLot.enums.ParkingSpotType;

public class ParkingSpotFactory {
    public static ParkingSpot create(String spotId, ParkingSpotType type) {
        return new ParkingSpot(spotId, type);
    }
}
