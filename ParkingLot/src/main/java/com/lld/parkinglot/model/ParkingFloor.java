package com.lld.parkinglot.model;

import com.lld.parkinglot.enums.ParkingSpotType;
import com.lld.parkinglot.model.factory.ParkingSpot;
import com.lld.parkinglot.model.factory.ParkingSpotFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ParkingFloor {
    private final Map<ParkingSpotType, List<ParkingSpot>> floorMap;
    private final int floorNumber;
    private final ReadWriteLock floorLock;
    private final Map<ParkingSpotType, AtomicInteger> availableCounts;
    private static final Logger logger = Logger.getLogger(ParkingFloor.class.getName());
    
    public ParkingFloor(Map<ParkingSpotType, Integer> floorConfig, int floor) {
        this.floorMap = new ConcurrentHashMap<>();
        this.floorNumber = floor;
        this.floorLock = new ReentrantReadWriteLock(true);
        this.availableCounts = new ConcurrentHashMap<>();
        
        for (Map.Entry<ParkingSpotType, Integer> entry : floorConfig.entrySet()) {
            ParkingSpotType type = entry.getKey();
            int count = entry.getValue();

            List<ParkingSpot> spots = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                String spotId = floor + "_" + type + "_" + i;
                spots.add(ParkingSpotFactory.create(spotId, type));
            }

            floorMap.put(type, Collections.synchronizedList(spots));
            availableCounts.put(type, new AtomicInteger(count));
        }
        
        logger.info("ParkingFloor " + floor + " initialized with " + floorConfig.size() + " spot types");
    }
    public ParkingSpot getParkingSpotById(String id){
        try {
            floorLock.readLock().lock();
            ParkingSpotType type = ParkingSpotType.valueOf(id.split("_")[1]);
            int index = Integer.parseInt(id.split("_")[2]);
            List<ParkingSpot> spots = floorMap.get(type);
            if (spots != null && index > 0 && index <= spots.size()) {
                return spots.get(index - 1);
            }
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing spot ID: " + id, e);
            return null;
        } finally {
            floorLock.readLock().unlock();
        }
    }
    public ParkingSpot getAvailableSpotOfType(ParkingSpotType type) {
        try {
            floorLock.readLock().lock();
            List<ParkingSpot> spots = floorMap.get(type);
            if (spots == null) {
                return null;
            }
            synchronized (spots) {
                return spots.stream()
                        .filter(s -> !s.isOccupied())
                        .findFirst()
                        .orElse(null);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding available spot of type " + type, e);
            return null;
        } finally {
            floorLock.readLock().unlock();
        }
    }
    public void printFloorDetails() {
        try {
            floorLock.readLock().lock();
            for (Map.Entry<ParkingSpotType, List<ParkingSpot>> entry : floorMap.entrySet()) {
                ParkingSpotType type = entry.getKey();
                List<ParkingSpot> spots = entry.getValue();
                int occupied = 0;
                
                synchronized (spots) {
                    for (ParkingSpot spot : spots) {
                        if (spot.isOccupied()) {
                            occupied++;
                        }
                    }
                }
            }
        } finally {
            floorLock.readLock().unlock();
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }
    
    public Map<ParkingSpotType, List<ParkingSpot>> getFloorMap() {
        try {
            floorLock.readLock().lock();
            return new ConcurrentHashMap<>(floorMap);
        } finally {
            floorLock.readLock().unlock();
        }
    }
    
    public int getAvailableCount(ParkingSpotType type) {
        AtomicInteger count = availableCounts.get(type);
        return count != null ? count.get() : 0;
    }
    
    public void updateAvailableCount(ParkingSpotType type, int delta) {
        AtomicInteger count = availableCounts.get(type);
        if (count != null) {
            count.addAndGet(delta);
            logger.fine("Updated available count for " + type + " on floor " + floorNumber + 
                ": " + count.get());
        }
    }
    
    public Map<ParkingSpotType, Integer> getAllAvailableCounts() {
        Map<ParkingSpotType, Integer> result = new HashMap<>();
        for (Map.Entry<ParkingSpotType, AtomicInteger> entry : availableCounts.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }
        return result;
    }
    
    public int getTotalOccupiedSpots() {
        try {
            floorLock.readLock().lock();
            int totalOccupied = 0;
            for (List<ParkingSpot> spots : floorMap.values()) {
                synchronized (spots) {
                    for (ParkingSpot spot : spots) {
                        if (spot.isOccupied()) {
                            totalOccupied++;
                        }
                    }
                }
            }
            return totalOccupied;
        } finally {
            floorLock.readLock().unlock();
        }
    }
}