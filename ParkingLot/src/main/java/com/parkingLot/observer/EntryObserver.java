package main.java.com.parkingLot.observer;

import main.java.com.parkingLot.model.ParkingSession;

public interface EntryObserver {
    void onVehicleEntry(ParkingSession session);
}
