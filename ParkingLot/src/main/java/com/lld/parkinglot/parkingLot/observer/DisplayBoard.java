package main.java.com.lld.parkinglot.observer;

import main.java.com.lld.parkinglot.model.ParkingSession;

public class DisplayBoard implements ParkingObserver {
    @Override
    public void onVehicleEntry(ParkingSession session) {
        System.out.println(session.getUser().getVehicle().getNumberPlate()+" parked in "+session.getSpot().getSpotId());
    }

    @Override
    public void onVehicleExit(ParkingSession session) {
        System.out.println(session.getUser().getVehicle().getNumberPlate()+" exited from "+session.getSpot().getSpotId());
    }
}