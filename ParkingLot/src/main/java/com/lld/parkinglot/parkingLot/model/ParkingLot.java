package main.java.com.lld.parkinglot.model;

import main.java.com.lld.parkinglot.enums.ParkingSpotType;
import main.java.com.lld.parkinglot.enums.VehicleType;
import main.java.com.lld.parkinglot.model.factory.ParkingLotManager;
import main.java.com.lld.parkinglot.model.factory.ParkingLotManagerFactory;
import main.java.com.lld.parkinglot.model.factory.ParkingSpot;
import main.java.com.lld.parkinglot.strategy.AllocationStrategy;
import main.java.com.lld.parkinglot.strategy.FeeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParkingLot {

    private final List<ParkingFloor> floors;
    private final Map<String, ParkingSession> activeSessions;
    private final Lock lock = new ReentrantLock(true);

    private final AllocationStrategy allocationStrategy;
    private final ParkingLotManager parkingLotManager;

    private static volatile ParkingLot lot;

    private static final int MAX_RETRIES = 3;
    private static final int LOCK_TIMEOUT_SECONDS = 2;

    private ParkingLot(
            AllocationStrategy allocationStrategy,
            ParkingLotManager parkingLotManager,
            int floorCount,
            List<Map<ParkingSpotType, Integer>> floorMap) {

        this.allocationStrategy = allocationStrategy;
        this.parkingLotManager = parkingLotManager;
        this.floors = new ArrayList<>();
        this.activeSessions = new ConcurrentHashMap<>();

        for (int i = 0; i < floorCount; i++) {
            floors.add(new ParkingFloor(floorMap.get(i), i));
        }
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
                if (spot != null) return spot;
            }
        }
        return null;
    }

    public void park(User user) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            boolean acquired = false;
            try {
                acquired = lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!acquired) {
                    attempts++;
                    continue;
                }
                ParkingSpot spot = findAvailableSlot(user.getVehicle().getVehicleType());
                if (spot == null) {
                    throw new RuntimeException("No parking spot available");
                }
                spot.parkVehicle(user.getVehicle(), allocationStrategy);
                ParkingSession session = new ParkingSession(user, spot);
                activeSessions.put(user.getVehicle().getNumberPlate(), session);
                parkingLotManager.notifyEntry(session);
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while parking", e);
            } finally {
                if (acquired) {
                    lock.unlock();
                }
            }
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
                    continue;
                }
                ParkingSession session = activeSessions.get(user.getVehicle().getNumberPlate());
                if (session == null) {
                    throw new RuntimeException("No active session");
                }
                session.endSession();
                session.getSpot().exitVehicle();
                parkingLotManager.notifyExit(session);
                activeSessions.remove(user.getVehicle().getNumberPlate());
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while exiting", e);
            } finally {
                if (acquired) {
                    lock.unlock();
                }
            }
        }
        throw new RuntimeException("System busy. Please try again later.");
    }
}