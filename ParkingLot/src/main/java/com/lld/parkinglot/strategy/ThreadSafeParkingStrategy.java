package com.lld.parkinglot.strategy;

import com.lld.parkinglot.enums.ParkingSpotType;
import com.lld.parkinglot.enums.VehicleType;
import com.lld.parkinglot.model.factory.ParkingSpot;
import com.lld.parkinglot.model.ParkingFloor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public class ThreadSafeParkingStrategy implements AllocationStrategy {
    
    private final Map<VehicleType, List<ParkingSpotType>> vehicleToSpotMapping;
    private final Map<String, AtomicInteger> spotAllocationCounts;
    private final ReadWriteLock strategyLock;
    
    public ThreadSafeParkingStrategy() {
        this.vehicleToSpotMapping = new ConcurrentHashMap<>();
        this.spotAllocationCounts = new ConcurrentHashMap<>();
        this.strategyLock = new ReentrantReadWriteLock(true); // Fair lock

        initializeDefaultMappings();
    }
    
        private void initializeDefaultMappings() {
        try {
            strategyLock.writeLock().lock();

            vehicleToSpotMapping.put(VehicleType.CAR, List.of(
                ParkingSpotType.LARGE,
                ParkingSpotType.BIG
            ));

            vehicleToSpotMapping.put(VehicleType.MOTORBIKE, List.of(
                ParkingSpotType.SMALL,
                ParkingSpotType.LARGE
            ));

            vehicleToSpotMapping.put(VehicleType.TRUCK, List.of(
                ParkingSpotType.LARGE
            ));
            
        } finally {
            strategyLock.writeLock().unlock();
        }
    }
    
        @Override
    public boolean supportsPark(VehicleType vehicleType, ParkingSpotType spotType) {
        try {
            strategyLock.readLock().lock();
            List<ParkingSpotType> supportedSpots = vehicleToSpotMapping.get(vehicleType);
            return supportedSpots != null && supportedSpots.contains(spotType);
        } finally {
            strategyLock.readLock().unlock();
        }
    }
    
        @Override
    public List<ParkingSpotType> getPreferredSpotOrder(VehicleType vehicleType) {
        try {
            strategyLock.readLock().lock();
            List<ParkingSpotType> preferredOrder = vehicleToSpotMapping.get(vehicleType);
            if (preferredOrder == null) {
                return List.of();
            }

            return List.copyOf(preferredOrder);
        } finally {
            strategyLock.readLock().unlock();
        }
    }
    
        public void recordSpotAllocation(ParkingSpotType spotType) {
        spotAllocationCounts.computeIfAbsent(spotType.name(), k -> new AtomicInteger(0))
            .incrementAndGet();
    }
    
        public Map<String, Integer> getAllocationStatistics() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        spotAllocationCounts.forEach((key, value) -> stats.put(key, value.get()));
        return stats;
    }
    
        public void updateVehicleMapping(VehicleType vehicleType, List<ParkingSpotType> spotTypes) {
        try {
            strategyLock.writeLock().lock();
            vehicleToSpotMapping.put(vehicleType, List.copyOf(spotTypes));
        } finally {
            strategyLock.writeLock().unlock();
        }
    }
    
        public ParkingSpot findOptimalSpot(List<ParkingFloor> floors, VehicleType vehicleType) {
        List<ParkingSpotType> preferredOrder = getPreferredSpotOrder(vehicleType);
        
        // Search through preferred spot types in order
        for (ParkingSpotType spotType : preferredOrder) {
            for (ParkingFloor floor : floors) {
                ParkingSpot spot = floor.getAvailableSpotOfType(spotType);
                if (spot != null) {
                    recordSpotAllocation(spotType);
                    return spot;
                }
            }
        }
        
        return null;
    }
    
        public void resetStatistics() {
        spotAllocationCounts.clear();
    }
    
        public Map<VehicleType, List<ParkingSpotType>> getCurrentConfiguration() {
        try {
            strategyLock.readLock().lock();
            Map<VehicleType, List<ParkingSpotType>> config = new ConcurrentHashMap<>();
            vehicleToSpotMapping.forEach((key, value) -> config.put(key, List.copyOf(value)));
            return config;
        } finally {
            strategyLock.readLock().unlock();
        }
    }
}
