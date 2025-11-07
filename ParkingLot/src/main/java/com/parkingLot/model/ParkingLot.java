package main.java.com.parkingLot.model;

import main.java.com.parkingLot.enums.ParkingSpotType;
import main.java.com.parkingLot.enums.VehicleType;
import main.java.com.parkingLot.model.factory.ParkingSpot;
import main.java.com.parkingLot.model.factory.Vehicle;
import main.java.com.parkingLot.observer.DisplaBoard;
import main.java.com.parkingLot.observer.LoggingSystem;
import main.java.com.parkingLot.observer.ParkingLotManager;
import main.java.com.parkingLot.observer.UserPaymentSystem;
import main.java.com.parkingLot.strategy.AllocationStrategy;
import main.java.com.parkingLot.strategy.FeeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParkingLot {
    private final List<ParkingFloor> floors;
    private final Map<String, ParkingSession> activeSessions;
    private final Lock lock = new ReentrantLock();
    private final AllocationStrategy allocationStrategy;
    private final ParkingLotManager parkingLotManager;
    private static ParkingLot lot;

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
            ParkingFloor floor = new ParkingFloor(floorMap.get(i), i);
            floors.add(floor);
        }
    }
    public static ParkingLot getInstance() {
        if (lot != null) {
            return lot;
        } else {
            throw new IllegalStateException("model.ParkingLot instance not created!");
        }
    }
    public static ParkingLot createInstance(FeeStrategy feeStrategy,
                                            AllocationStrategy allocationStrategy,
                                            int floorCount,
                                            List<Map<ParkingSpotType, Integer>> floorMap) {
        if (lot == null) {
            lot = new ParkingLot(allocationStrategy, createParkingLotManager(feeStrategy), floorCount, floorMap);
        }
        return lot;
    }

    private static ParkingLotManager createParkingLotManager(FeeStrategy feeStrategy) {
        ParkingLotManager parkingLotManager = new ParkingLotManager();
        LoggingSystem loggingSystem = new LoggingSystem("lotTesting");
        UserPaymentSystem userPaymentSystem = new UserPaymentSystem(feeStrategy);
        DisplaBoard displaBoard = new DisplaBoard();
        parkingLotManager.registerExit(loggingSystem);
        parkingLotManager.registerExit(userPaymentSystem);
        parkingLotManager.registerExit(displaBoard);
        parkingLotManager.registerEntry(loggingSystem);
        parkingLotManager.registerEntry(displaBoard);
        return parkingLotManager;
    }

    public ParkingSpot getAvailableSlot(VehicleType vehicleType) {
        lock.lock();
        try {
            List<ParkingSpotType> preferredTypes = allocationStrategy.getPreferredSpotOrder(vehicleType);
            for (ParkingSpotType spotType : preferredTypes) {
                for (ParkingFloor floor : floors) {
                    ParkingSpot available = floor.getAvailableSpotOfType(spotType);
                    if (available != null) {
                        return available;
                    }
                }
            }
            return null;
        }finally {
            lock.unlock();
        }
    }

    public void park(User user) {
        lock.lock();
        try {
            ParkingSpot spot = getAvailableSlot(user.getVehicle().getVehicleType());
            if(spot == null) return;
            spot.parkVehicle(user.getVehicle());
            ParkingSession parkingSession = new ParkingSession(user,spot);
            activeSessions.put(user.getVehicle().getNumberPlate(),parkingSession);
            parkingLotManager.notifyEntry(parkingSession);
        } catch (Exception e) {
            System.out.println("Error occurred");
            System.out.println("can't park the vehicle: "+user.getVehicle().getNumberPlate());
        }finally {
            lock.unlock();
        }
    }

    public void exit(User user) {
        lock.lock();
        try {
            Vehicle bookedVehicle = user.getVehicle();
            ParkingSession parkingSession = activeSessions.get(bookedVehicle.getNumberPlate());
            parkingSession.endSession();
            parkingLotManager.notifyExit(parkingSession);
            activeSessions.remove(bookedVehicle.getNumberPlate());
        }finally {
            lock.unlock();
        }
    }
    public AllocationStrategy getAllocationStrategy() {
        return allocationStrategy;
    }
}