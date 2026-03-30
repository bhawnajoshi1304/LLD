package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.Direction;

public class IdleState implements ElevatorState {
    @Override
    public void onEnter(Elevator elevator) {
        elevator.setDirection(Direction.IDLE);
        System.out.println("Elevator " + elevator.getId() + " is idle");
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
        return !elevator.getRequestsQueue().isEmpty();
    }

    @Override
    public void handleRequest(Elevator elevator, int floor) {
        elevator.addRequest(new com.lld.elevator.model.InternalRequest(elevator.getId(), floor));
    }

    @Override
    public String getStateName() {
        return "IDLE";
    }
}
