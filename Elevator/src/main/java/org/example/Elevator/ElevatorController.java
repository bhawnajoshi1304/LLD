package org.example.Elevator;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ElevatorController {
    List<Elevator> elevators;
    List<Floor> floors;
    SchedulingStrategy schedulingStrategy;
    int currentFloorId;
    public void requestFloor(int elevatorId, int floorNumber) {
        Elevator elevator = getElevatorById(elevatorId);
        System.out.println("Internal request: Elevator " + elevator.getId() + " to floor " + floorNumber);
        Direction direction = floorNumber > elevator.getCurrentFloor() ? Direction.UP : Direction.DOWN;
        // Add the request to the elevator
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
