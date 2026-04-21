package com.lld.elevator.state;

import com.lld.elevator.model.Elevator;

public interface ElevatorState {
    void onEnter(Elevator elevator);
    String getStateName();
    ElevatorState step(Elevator elevator);
}
