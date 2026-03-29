package org.example.Elevator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalRequest {
    private int elevatorId;
    private int floorNumber;
    private Direction direction;
    public ExternalRequest(int elevatorId, int floorNumber, boolean b, Direction direction) {
        this.elevatorId = elevatorId;
        this.floorNumber = floorNumber;
        this.direction = direction;
    }
}
