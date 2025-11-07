package main.java.com.parkingLot.model;

import main.java.com.parkingLot.enums.ParkingSpotType;
import main.java.com.parkingLot.model.factory.ParkingSpot;
import main.java.com.parkingLot.model.factory.ParkingSpotFactory;

import java.util.*;

public class ParkingFloor {
    Map<ParkingSpotType, List<ParkingSpot>> floorMap;
    int floorNumber;
    public ParkingFloor(Map<ParkingSpotType, Integer> floorConfig, int floor) {
        this.floorMap = new HashMap<>();
        this.floorNumber = floor;
        for (Map.Entry<ParkingSpotType, Integer> entry : floorConfig.entrySet()) {
            ParkingSpotType type = entry.getKey();
            int count = entry.getValue();

            List<ParkingSpot> spots = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                String spotId = floor + "_" + type + "_" + i; // Example: CAR_1, CAR_2, etc.
                spots.add(ParkingSpotFactory.createSpot(spotId, type));
            }

            floorMap.put(type, spots);
        }
    }
    public ParkingSpot getParkingSpotById(String id){
        ParkingSpotType type = ParkingSpotType.valueOf(id.split("_")[1]);
        int index = Integer.parseInt(id.split("_")[2]);
        return floorMap.get(type).get(index-1);
    }
    public ParkingSpot getAvailableSpotOfType(ParkingSpotType type) {
        List<ParkingSpot> spots = floorMap.get(type);
        if (spots == null) return null;

        return spots.stream()
                .filter(s -> !s.isOccupied())
                .findFirst()
                .orElse(null);
    }
    public void printFloorDetails() {
        for (Map.Entry<ParkingSpotType, List<ParkingSpot>> entry : floorMap.entrySet()) {
            System.out.println(entry.getKey() + " spots: " + entry.getValue().size());
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }
}