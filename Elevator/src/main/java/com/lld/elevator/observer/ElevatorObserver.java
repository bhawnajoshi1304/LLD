package com.lld.elevator.observer;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.ElevatorState;

public interface ElevatorObserver {
    void onElevatorFloorChanged(Elevator elevator, int floor);
    void onElevatorStateChanged(Elevator elevator, ElevatorState state);
}
