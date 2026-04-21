package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;

public class MovingDownState implements ElevatorState {
    public static final MovingDownState INSTANCE = new MovingDownState();
    @Override
    public void onEnter(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " is moving down");
    }
    @Override
    public String getStateName() {
        return "MOVING DOWN";
    }

    @Override
    public ElevatorState step(Elevator elevator) {
        if (!elevator.hasPendingRequests()) {
            return IdleState.INSTANCE;
        }
        int nextFloor = elevator.getStrategy().getNextFloor(elevator);
        if (nextFloor > elevator.getCurrentFloor()) {
            return MovingUpState.INSTANCE;
        }
        if (nextFloor < elevator.getCurrentFloor()) {
            elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
            elevator.notifyFloorObservers();
        }
        int nextStop = elevator.getStrategy().getNextStop(elevator);
        if (nextStop == elevator.getCurrentFloor()) {
            elevator.getStrategy().removeReachedFloor(elevator.getCurrentFloor());
            return StoppedState.INSTANCE;
        }
        return this;
    }
}
