package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;

public class MovingUpState implements ElevatorState {
    public static final MovingUpState INSTANCE = new MovingUpState();
    @Override
    public void onEnter(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " is moving up");
    }
    @Override
    public String getStateName() {
        return "MOVING UP";
    }

    @Override
    public ElevatorState step(Elevator elevator) {
        try {
            if (!elevator.hasPendingRequests()) {
                return IdleState.INSTANCE;
            }
            int nextFloor = elevator.getStrategy().getNextFloor(elevator);
            if (nextFloor < elevator.getCurrentFloor()) {
                return MovingDownState.INSTANCE;
            }
            if (nextFloor > elevator.getCurrentFloor()) {
                elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
                elevator.notifyFloorObservers();
            }
            int nextStop = elevator.getStrategy().getNextStop(elevator);
            if (nextStop == elevator.getCurrentFloor()) {
                elevator.getStrategy().removeReachedFloor(elevator.getCurrentFloor());
                return StoppedState.INSTANCE;
            }
            
            return this;
        } catch (Exception e) {
            System.err.println("Error in MovingUpState for elevator " + elevator.getId() + ": " + e.getMessage());
            return IdleState.INSTANCE;
        }
    }
}
