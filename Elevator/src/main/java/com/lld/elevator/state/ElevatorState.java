package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;

public interface ElevatorState {
    void onEnter(Elevator elevator);
    void onExit(Elevator elevator);
    boolean canAcceptRequest(Elevator elevator);
    boolean canMove(Elevator elevator);
    void handleRequest(Elevator elevator, int floor);
    String getStateName();
}
