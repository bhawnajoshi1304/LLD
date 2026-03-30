package main.java.com.lld.parkinglot.model.factory;

import main.java.com.lld.parkinglot.model.ParkingSession;
import main.java.com.lld.parkinglot.observer.ParkingObserver;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotManager {
    private final List<ParkingObserver> exitObservers;
    private final List<ParkingObserver> entryObservers;
    public void registerExit(ParkingObserver obs){
        if(obs!=null) {
            exitObservers.add(obs);
        }
    }
    public void registerEntry(ParkingObserver obs){
        if(obs!=null) {
            entryObservers.add(obs);
        }
    }
    public void notifyExit(ParkingSession parkingSession){
        exitObservers.forEach(obs->obs.onVehicleExit(parkingSession));
    }
    public void notifyEntry(ParkingSession parkingSession){
        entryObservers.forEach(obs->obs.onVehicleEntry(parkingSession));
    }
    public ParkingLotManager() {
        this.entryObservers = new ArrayList<>();
        this.exitObservers = new ArrayList<>();
    }
}