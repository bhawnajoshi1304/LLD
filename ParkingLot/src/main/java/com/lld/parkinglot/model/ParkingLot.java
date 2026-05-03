package com.lld.parkinglot.model;

import com.lld.parkinglot.enums.ParkingSpotType;
import com.lld.parkinglot.enums.VehicleType;
import com.lld.parkinglot.model.factory.ParkingLotManager;
import com.lld.parkinglot.model.factory.ParkingLotManagerFactory;
import com.lld.parkinglot.model.factory.ParkingSpot;
import com.lld.parkinglot.strategy.AllocationStrategy;
import com.lld.parkinglot.strategy.FeeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ParkingLot {

    private final List<ParkingFloor> floors;
    private final Map<String, ParkingSession> activeSessions;
    private final Lock lock = new ReentrantLock(true);
    private final Map<String, Lock> spotLocks = new ConcurrentHashMap<>();
    private final AtomicInteger totalCapacity;
    private final AtomicInteger occupiedSpots;
    private final AtomicBoolean maintenanceMode;

    private final AllocationStrategy allocationStrategy;
    private final ParkingLotManager parkingLotManager;

    private static volatile ParkingLot lot;
    private static final Logger logger = Logger.getLogger(ParkingLot.class.getName());

    private static final int MAX_RETRIES = 3;
    private static final int LOCK_TIMEOUT_SECONDS = 2;
    private static final int SPOT_LOCK_TIMEOUT_SECONDS = 1;
    
    private final AtomicInteger consecutiveErrors = new AtomicInteger(0);
    private final AtomicInteger totalErrors = new AtomicInteger(0);
    private volatile boolean circuitBreakerOpen = false;
    private volatile long circuitBreakerOpenTime = 0;
    private static final int CIRCUIT_BREAKER_THRESHOLD = 5;
    private static final long CIRCUIT_BREAKER_TIMEOUT_MS = 30000;

    private ParkingLot(
            AllocationStrategy allocationStrategy,
            ParkingLotManager parkingLotManager,
            int floorCount,
            List<Map<ParkingSpotType, Integer>> floorMap) {

        this.allocationStrategy = allocationStrategy;
        this.parkingLotManager = parkingLotManager;
        this.floors = new ArrayList<>();
        this.activeSessions = new ConcurrentHashMap<>();
        this.totalCapacity = new AtomicInteger(0);
        this.occupiedSpots = new AtomicInteger(0);
        this.maintenanceMode = new AtomicBoolean(false);

        for (int i = 0; i < floorCount; i++) {
            ParkingFloor floor = new ParkingFloor(floorMap.get(i), i);
            floors.add(floor);
            
            // Initialize spot locks
            for (Map.Entry<ParkingSpotType, Integer> entry : floorMap.get(i).entrySet()) {
                for (int j = 1; j <= entry.getValue(); j++) {
                    String spotId = i + "_" + entry.getKey() + "_" + j;
                    spotLocks.put(spotId, new ReentrantLock(true));
                    totalCapacity.incrementAndGet();
                }
            }
        }
        
        logger.info("ParkingLot initialized with " + totalCapacity.get() + " total capacity");
    }

    public static ParkingLot createInstance(
            FeeStrategy feeStrategy,
            AllocationStrategy allocationStrategy,
            int floorCount,
            List<Map<ParkingSpotType, Integer>> floorMap) {

        if (lot == null) {
            synchronized (ParkingLot.class) {
                if (lot == null) {
                    lot = new ParkingLot(
                            allocationStrategy,
                            ParkingLotManagerFactory.create(feeStrategy),
                            floorCount,
                            floorMap
                    );
                }
            }
        }
        return lot;
    }

    public static ParkingLot getInstance() {
        if (lot == null) {
            throw new IllegalStateException("ParkingLot not initialized");
        }
        return lot;
    }

    private ParkingSpot findAvailableSlot(VehicleType type) {
        for (ParkingSpotType spotType :
                allocationStrategy.getPreferredSpotOrder(type)) {
            for (ParkingFloor floor : floors) {
                ParkingSpot spot = floor.getAvailableSpotOfType(spotType);
                if (spot != null) {
                    // Double-check availability with spot-specific lock
                    Lock spotLock = spotLocks.get(spot.getSpotId());
                    if (spotLock != null) {
                        try {
                            if (spotLock.tryLock(SPOT_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                                try {
                                    if (!spot.isOccupied()) {
                                        return spot;
                                    }
                                } finally {
                                    spotLock.unlock();
                                }
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            logger.warning("Interrupted while trying to lock spot " + spot.getSpotId());
                        }
                    }
                }
            }
        }
        return null;
    }

    public void park(User user) {
        if (maintenanceMode.get()) {
            throw new RuntimeException("Parking lot is under maintenance");
        }
        
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            boolean acquired = false;
            try {
                acquired = lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!acquired) {
                    attempts++;
                    logger.warning("Failed to acquire lock for parking attempt " + attempts);
                    continue;
                }
                
                // Check capacity first
                if (occupiedSpots.get() >= totalCapacity.get()) {
                    throw new RuntimeException("Parking lot is full");
                }
                
                ParkingSpot spot = findAvailableSlot(user.getVehicle().getVehicleType());
                if (spot == null) {
                    throw new RuntimeException("No parking spot available for vehicle type: " + 
                        user.getVehicle().getVehicleType());
                }
                
                // Acquire spot-specific lock to prevent double booking
                Lock spotLock = spotLocks.get(spot.getSpotId());
                if (spotLock.tryLock(SPOT_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    try {
                        // Final check after acquiring spot lock
                        if (!spot.isOccupied()) {
                            spot.parkVehicle(user.getVehicle());
                            ParkingSession session = new ParkingSession(user, spot);
                            activeSessions.put(user.getVehicle().getNumberPlate(), session);
                            occupiedSpots.incrementAndGet();
                            parkingLotManager.notifyEntry(session);
                            
                            logger.info("Vehicle " + user.getVehicle().getNumberPlate() + 
                                " parked at spot " + spot.getSpotId());
                            return;
                        } else {
                            logger.warning("Spot " + spot.getSpotId() + " was just occupied");
                        }
                    } finally {
                        spotLock.unlock();
                    }
                } else {
                    logger.warning("Failed to acquire spot lock for " + spot.getSpotId());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warning("Thread interrupted while parking vehicle " + user.getVehicle().getNumberPlate());
                throw new RuntimeException("Thread interrupted while parking", e);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during parking vehicle " + user.getVehicle().getNumberPlate(), e);
                
                // Implement circuit breaker pattern
                if (shouldEnterCircuitBreakerMode()) {
                    enterCircuitBreakerMode();
                    throw new RuntimeException("Parking lot temporarily unavailable due to system errors", e);
                }
                
                throw e;
            } finally {
                if (acquired) {
                    lock.unlock();
                }
            }
            attempts++;
        }

        throw new RuntimeException("System busy. Please try again later.");
    }

    public void exit(User user) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            boolean acquired = false;
            try {
                acquired = lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!acquired) {
                    attempts++;
                    logger.warning("Failed to acquire lock for exit attempt " + attempts);
                    continue;
                }
                
                ParkingSession session = activeSessions.get(user.getVehicle().getNumberPlate());
                if (session == null) {
                    throw new RuntimeException("No active session found for vehicle: " + 
                        user.getVehicle().getNumberPlate());
                }
                
                ParkingSpot spot = session.getSpot();
                Lock spotLock = spotLocks.get(spot.getSpotId());
                if (spotLock.tryLock(SPOT_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    try {
                        session.endSession();
                        spot.exitVehicle();
                        parkingLotManager.notifyExit(session);
                        activeSessions.remove(user.getVehicle().getNumberPlate());
                        occupiedSpots.decrementAndGet();
                        
                        logger.info("Vehicle " + user.getVehicle().getNumberPlate() + 
                            " exited from spot " + spot.getSpotId());
                        return;
                    } finally {
                        spotLock.unlock();
                    }
                } else {
                    logger.warning("Failed to acquire spot lock for exit: " + spot.getSpotId());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while exiting", e);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during exit", e);
                throw e;
            } finally {
                if (acquired) {
                    lock.unlock();
                }
            }
            attempts++;
        }
        throw new RuntimeException("System busy. Please try again later.");
    }
    
    public int getAvailableSpots() {
        return totalCapacity.get() - occupiedSpots.get();
    }
    
    public int getTotalCapacity() {
        return totalCapacity.get();
    }
    
    public int getOccupiedSpots() {
        return occupiedSpots.get();
    }
    
    public boolean isFull() {
        return occupiedSpots.get() >= totalCapacity.get();
    }
    
    public void setMaintenanceMode(boolean enabled) {
        maintenanceMode.set(enabled);
        logger.info("Parking lot maintenance mode: " + enabled);
    }
    
    public boolean isInMaintenanceMode() {
        return maintenanceMode.get();
    }
    
    // Circuit breaker pattern methods
    private boolean shouldEnterCircuitBreakerMode() {
        int errors = consecutiveErrors.incrementAndGet();
        totalErrors.incrementAndGet();
        
        if (errors >= CIRCUIT_BREAKER_THRESHOLD) {
            return true;
        }
        return false;
    }
    
    private void enterCircuitBreakerMode() {
        circuitBreakerOpen = true;
        circuitBreakerOpenTime = System.currentTimeMillis();
        logger.warning("Parking lot circuit breaker opened due to " + consecutiveErrors.get() + " consecutive errors");

        java.util.concurrent.ScheduledExecutorService scheduler = 
            java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if (circuitBreakerOpen && 
                System.currentTimeMillis() - circuitBreakerOpenTime >= CIRCUIT_BREAKER_TIMEOUT_MS) {
                circuitBreakerOpen = false;
                consecutiveErrors.set(0);
                logger.info("Parking lot circuit breaker closed - service restored");
            }
            scheduler.shutdown();
        }, CIRCUIT_BREAKER_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    
    public boolean isCircuitBreakerOpen() {
        return circuitBreakerOpen;
    }
    
    public int getTotalErrors() {
        return totalErrors.get();
    }
    
    public int getConsecutiveErrors() {
        return consecutiveErrors.get();
    }

    public boolean isHealthy() {
        return !circuitBreakerOpen && !maintenanceMode.get() && !isFull();
    }
    
    public Map<String, Integer> getOccupancyByFloor() {
        Map<String, Integer> occupancy = new ConcurrentHashMap<>();
        for (ParkingFloor floor : floors) {
            int floorOccupied = 0;
            for (Map.Entry<ParkingSpotType, List<ParkingSpot>> entry : floor.getFloorMap().entrySet()) {
                for (ParkingSpot spot : entry.getValue()) {
                    if (spot.isOccupied()) {
                        floorOccupied++;
                    }
                }
            }
            occupancy.put("Floor_" + floor.getFloorNumber(), floorOccupied);
        }
        return occupancy;
    }
}