package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.Direction;

public class MovingState implements ElevatorState {
    @Override
    public void onEnter(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " is moving " + elevator.getDirection());
    }

    @Override
    public void onExit(Elevator elevator) {
    }

    @Override
    public boolean canAcceptRequest(Elevator elevator) {
        return true;
    }

    @Override
    public boolean canMove(Elevator elevator) {
        return true;
    }

    @Override
    public void handleRequest(Elevator elevator, int floor) {
        elevator.addRequest(new com.lld.elevator.model.InternalRequest(elevator.getId(), floor));
    }

    @Override
    public String getStateName() {
        return "MOVING";
    }
}
