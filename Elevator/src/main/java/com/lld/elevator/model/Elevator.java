package com.lld.elevator.model;

import com.lld.elevator.observer.ElevatorObserver;
import com.lld.elevator.state.ElevatorState;
import com.lld.elevator.state.IdleState;
import com.lld.elevator.strategy.SchedulingStrategy;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Elevator {
    private final int id;
    private final int minFloor;
    private final int maxFloor;
    @Setter
    private int currentFloor;
    private ElevatorState state;
    private final SchedulingStrategy strategy;
    private final List<ElevatorObserver> stateObservers;
    private final List<ElevatorObserver> floorObservers;
    
    public Elevator(int id, int noOfFloors, SchedulingStrategy strategy) {
        this.id = id;
        this.currentFloor = 0;
        this.minFloor = 0;
        this.maxFloor = noOfFloors - 1;
        this.state = IdleState.INSTANCE;
        this.strategy = strategy;
        this.stateObservers = new ArrayList<>();
        this.floorObservers = new ArrayList<>();
    }
    
    public void addRequest(ElevatorRequest request) {
        int floor = request.getFloor();
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException("Invalid floor request: " + floor + 
                ". Valid range: " + minFloor + "-" + maxFloor);
        }
        strategy.addRequest(request, this);
    }
    
    public void step() {
        ElevatorState newState = state.step(this);
        if (newState != state) {
            state = newState;
            state.onEnter(this);
            notifyStateObservers();
        }
    }
    
    public boolean hasPendingRequests() {
        return strategy.hasRequests();
    }
    
    public void addStateObserver(ElevatorObserver observer) {
        stateObservers.add(observer);
    }
    
    public void addFloorObserver(ElevatorObserver observer) {
        floorObservers.add(observer);
    }
    
    public void notifyFloorObservers() {
        for (ElevatorObserver observer : floorObservers) {
            observer.onElevatorFloorChanged(currentFloor);
        }
    }
    
    private void notifyStateObservers() {
        for (ElevatorObserver observer : stateObservers) {
            observer.onElevatorStateChanged(state);
        }
    }
}
