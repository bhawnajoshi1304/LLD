package main.java.com.parkingLot.observer;

import main.java.com.parkingLot.model.ParkingSession;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotManager {
    private final List<ExitObserver> observers;
    private final List<EntryObserver> entryObservers;
    public void registerExit(ExitObserver obs){
        observers.add(obs);
    }
    public void registerEntry(EntryObserver obs){
        entryObservers.add(obs);
    }
    public void notifyExit(ParkingSession parkingSession){
        observers.forEach(obs->obs.onVehicleExit(parkingSession));
    }
    public void notifyEntry(ParkingSession parkingSession){
        entryObservers.forEach(obs->obs.onVehicleEntry(parkingSession));
    }
    public ParkingLotManager() {
        this.entryObservers = new ArrayList<>();
        this.observers = new ArrayList<>();
    }
}