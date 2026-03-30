package com.lld.parkinglot.observer;

import com.lld.parkinglot.model.ParkingSession;

public interface ParkingObserver {
    void onVehicleEntry(ParkingSession session);
    void onVehicleExit(ParkingSession session);

}