package com.lld.elevator.model;

import com.lld.elevator.mediator.HallPanel;
import com.lld.elevator.mediator.ElevatorPanel;
import com.lld.elevator.observer.ElevatorDisplay;
import com.lld.elevator.observer.ElevatorLogger;
import com.lld.elevator.strategy.LookSchedulingStrategy;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Building {
    private final String name;
    private final List<Floor> floors;
    private final ElevatorController elevatorController;
    private List<HallPanel> hallPanels;
    private List<ElevatorPanel> elevatorPanels;
    public Building(String name, int noOfFloors, int noOfElevators){
        this.name = name;
        List<Elevator> elevators = new ArrayList<>();
        for(int i=0;i<noOfElevators;i+=1){
            elevators.add(new Elevator(i,noOfFloors,new LookSchedulingStrategy()));
        }
        this.elevatorController = new ElevatorController(elevators, new LookSchedulingStrategy());
        setupObservers(elevators, name);
        this.floors = new ArrayList<>();
        for(int i=0;i<noOfFloors;i+=1){
            floors.add(new Floor(i,noOfElevators,elevators));
        }
        setupPanels(elevators);
    }
    
    private void setupObservers(List<Elevator> elevators, String buildingName) {
        for(int i=0; i<elevators.size(); i++){
            Elevator elevator = elevators.get(i);
            ElevatorLogger logger = new ElevatorLogger(i, buildingName);
            ElevatorDisplay display = new ElevatorDisplay();
            elevator.addFloorObserver(logger);
            elevator.addStateObserver(logger);
            elevator.addFloorObserver(display);
            elevator.addStateObserver(display);
        }
    }
    
    private void setupPanels(List<Elevator> elevators) {
        hallPanels = new ArrayList<>();
        for (int i = 0; i < floors.size(); i++) {
            hallPanels.add(new HallPanel(elevators.get(0), i));
        }
        elevatorPanels = new ArrayList<>();
        for (Elevator elevator : elevators) {
            elevatorPanels.add(new ElevatorPanel(elevator, floors.size()));
        }
    }
    
    public Building(String name, int noOfFloors, List<Elevator> elevators){
        this.name = name;
        this.elevatorController = new ElevatorController(elevators, new LookSchedulingStrategy());
        setupObservers(elevators, name);
        this.floors = new ArrayList<>();
        for(int i=0;i<noOfFloors;i+=1){
            floors.add(new Floor(i,elevators.size(),elevators));
        }
        setupPanels(elevators);
    }
    
    public void step() {
        ElevatorLogger.incrementStepCounter();
        elevatorController.step();
    }
    
    public void addExternalRequest(int floor, Direction direction) {
        elevatorController.addRequest(new ExternalRequest(floor, direction));
    }
    
    public List<Elevator> getElevators() {
        return elevatorController.getElevators();
    }
    
    public boolean hasPendingRequests() {
        return elevatorController.getPendingRequestCount() > 0 || 
               elevatorController.getElevators().stream().anyMatch(Elevator::hasPendingRequests);
    }
    
    public boolean areAllElevatorsIdle() {
        return elevatorController.areAllElevatorsIdle() && !hasPendingRequests();
    }
}
