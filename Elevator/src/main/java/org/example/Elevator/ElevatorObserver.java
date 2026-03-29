package org.example.Elevator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public interface ElevatorObserver {
    void onElevatorFloorChanged(Elevator elevator, int floor);
    void onElevatorStateChanged(Elevator elevator, ElevatorState state);
}
