package com.lld.elevator.model;

import com.lld.elevator.observer.ElevatorObserver;
import com.lld.elevator.strategy.SchedulingStrategy;
import lombok.Getter;

import java.util.List;

public class ElevatorController {
    @Getter
    private final List<Elevator> elevators;
    private final SchedulingStrategy strategy;
    
    public ElevatorController(List<Elevator> elevators, SchedulingStrategy strategy) {
        this.elevators = elevators;
        this.strategy = strategy;
    }
    
    public void step() {
        for (Elevator elevator : elevators) {
            elevator.step();
        }
    }
    
    public void addRequest(ElevatorRequest request) {
        // Distribute request to all elevators - each strategy handles it differently
        for (Elevator elevator : elevators) {
            elevator.addRequest(request);
        }
    }
    
    public void addRequestToSpecificElevator(int elevatorId, ElevatorRequest request) {
        if (elevatorId >= 0 && elevatorId < elevators.size()) {
            elevators.get(elevatorId).addRequest(request);
        }
    }
    
    public void addObserver(ElevatorObserver observer) {
        for (Elevator elevator : elevators) {
            elevator.addStateObserver(observer);
            elevator.addFloorObserver(observer);
        }
    }
    
    public boolean areAllElevatorsIdle() {
        return elevators.stream()
            .allMatch(e -> !e.hasPendingRequests() && 
                         e.getState().getStateName().equals("IDLE"));
    }

}
