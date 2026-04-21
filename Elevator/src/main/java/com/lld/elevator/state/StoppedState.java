package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;

public class StoppedState implements ElevatorState {
    public static final StoppedState INSTANCE = new StoppedState();
    @Override
    public void onEnter(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " is stopped at floor " + elevator.getCurrentFloor());
    }

    @Override
    public String getStateName() {
        return "STOPPED";
    }

    @Override
    public ElevatorState step(Elevator elevator) {
        if (!elevator.hasPendingRequests()) {
            return IdleState.INSTANCE;
        }
        int nextFloor = elevator.getStrategy().getNextFloor(elevator);
        int currentFloor = elevator.getCurrentFloor();
        if (nextFloor > currentFloor) {
            return MovingUpState.INSTANCE;
        } else if (nextFloor < currentFloor) {
            return MovingDownState.INSTANCE;
        } else {
            return IdleState.INSTANCE;
        }
    }
}
