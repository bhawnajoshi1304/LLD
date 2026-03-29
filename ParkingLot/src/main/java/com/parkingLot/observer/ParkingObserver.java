package main.java.com.parkingLot.observer;

import main.java.com.parkingLot.model.ParkingSession;

public interface ParkingObserver {
    void onVehicleEntry(ParkingSession session);
    void onVehicleExit(ParkingSession session);

}