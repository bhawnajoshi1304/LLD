package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;

public class MaintenanceState implements ElevatorState {
    @Override
    public void onEnter(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " is under maintenance");
    }

    @Override
    public void onExit(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " maintenance complete");
    }

    @Override
    public boolean canAcceptRequest(Elevator elevator) {
        return false;
    }

    @Override
    public boolean canMove(Elevator elevator) {
        return false;
    }

    @Override
    public void handleRequest(Elevator elevator, int floor) {
        throw new IllegalStateException("Elevator is under maintenance");
    }

    @Override
    public String getStateName() {
        return "MAINTENANCE";
    }
}
