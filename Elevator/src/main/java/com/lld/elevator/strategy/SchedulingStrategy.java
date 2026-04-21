package com.lld.elevator.strategy;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.ElevatorRequest;

public interface SchedulingStrategy {
    void addRequest(ElevatorRequest elevatorRequest, Elevator elevator);
    int getNextStop(Elevator elevator);
    int getNextFloor(Elevator elevator);
    void removeReachedFloor(int floor);
    boolean hasRequests();
}
