package main.java.com.lld.parkinglot.observer;

import main.java.com.lld.parkinglot.model.ParkingSession;

public interface ParkingObserver {
    void onVehicleEntry(ParkingSession session);
    void onVehicleExit(ParkingSession session);

}