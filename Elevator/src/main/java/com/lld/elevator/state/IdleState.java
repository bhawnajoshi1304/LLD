package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;

public class IdleState implements ElevatorState {
    public static final IdleState INSTANCE = new IdleState();
    @Override
    public void onEnter(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " is idle");
    }
    @Override
    public String getStateName() {
        return "IDLE";
    }
    @Override
    public ElevatorState step(Elevator elevator) {
        try {
            if (elevator.hasPendingRequests()) {
                return MovingUpState.INSTANCE;
            }
            return this;
        } catch (Exception e) {
            System.err.println("Error in IdleState for elevator " + elevator.getId() + ": " + e.getMessage());
            return this; // Stay in idle state on error
        }
    }
}
