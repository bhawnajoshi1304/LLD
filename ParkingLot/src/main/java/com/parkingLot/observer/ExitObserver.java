package main.java.com.parkingLot.observer;

import main.java.com.parkingLot.model.ParkingSession;

public interface ExitObserver {
    void onVehicleExit(ParkingSession session);
}