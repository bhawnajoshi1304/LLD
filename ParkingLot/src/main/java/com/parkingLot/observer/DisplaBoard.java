package main.java.com.parkingLot.observer;

import main.java.com.parkingLot.model.ParkingSession;

public class DisplaBoard implements ExitObserver, EntryObserver {
    @Override
    public void onVehicleEntry(ParkingSession session) {
        System.out.println(session.getUser().getVehicle().getNumberPlate()+" parked in "+session.getSpot().getSpotId());
    }

    @Override
    public void onVehicleExit(ParkingSession session) {
        System.out.println(session.getUser().getVehicle().getNumberPlate()+" exited from "+session.getSpot().getSpotId());
    }
}