package org.example.Elevator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternalRequest implements ElevatorRequest, ElevatorCommand{
    private int elevatorId;
    private int floor;
    public InternalRequest(int elevatorId, int floorNumber) {
        this.elevatorId = elevatorId;
        this.floor = floor;
    }
    @Override
    public void execute() {
    }
}
