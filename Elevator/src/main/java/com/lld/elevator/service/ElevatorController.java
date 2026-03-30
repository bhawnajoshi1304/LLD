package com.lld.elevator.service;

import com.lld.elevator.model.*;
import com.lld.elevator.strategy.SchedulingStrategy;

import java.util.List;

public class ElevatorController {
    List<Elevator> elevators;
    List<Floor> floors;
    SchedulingStrategy schedulingStrategy;
    int currentFloorId;
    public void requestFloor(int elevatorId, int floorNumber) {
        Elevator elevator = getElevatorById(elevatorId);
        System.out.println("Internal request: Elevator " + elevator.getId() + " to floor " + floorNumber);
        Direction direction = floorNumber > elevator.getCurrentFloor() ? Direction.UP : Direction.DOWN;
        elevator.addRequest(
                new InternalRequest(elevatorId, floorNumber));
    }
    public void requestElevator(int elevatorId, int floorNumber, Direction direction) {
        System.out.println("External request: Floor " + floorNumber + ", Direction " + direction);
        Elevator selectedElevator = getElevatorById(elevatorId);
        if (selectedElevator != null) {
            selectedElevator.addRequest(
                    new ExternalRequest(elevatorId, floorNumber, false, direction));
            System.out.println("Assigned elevator " + selectedElevator.getId()
                    + " to floor " + floorNumber);
        } else {
            // If no suitable elevator is found
            System.out.println("No elevator available for floor " + floorNumber);
        }
    }

    private Elevator getElevatorById(int elevatorId) {
        return this.elevators.get(elevatorId);
    }
}
