package com.lld.elevator.model;

public record ExternalRequest(int floor, Direction direction) implements ElevatorRequest {
    @Override
    public Integer getFloor() {
        return floor;
    }
}
