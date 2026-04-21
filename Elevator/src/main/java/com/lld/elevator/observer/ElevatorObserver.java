package com.lld.elevator.observer;

import com.lld.elevator.state.ElevatorState;

public interface ElevatorObserver {
    void onElevatorFloorChanged( int floor);
    void onElevatorStateChanged( ElevatorState state);
}
