package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;

public class MaintenanceState implements ElevatorState {
    @Override
    public void onEnter(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " is under maintenance");
    }
    @Override
    public String getStateName() {
        return "MAINTENANCE";
    }
    @Override
    public ElevatorState step(Elevator elevator) {
        return this;
    }
}
