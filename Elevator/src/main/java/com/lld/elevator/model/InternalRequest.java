package com.lld.elevator.model;

public record InternalRequest(int floor) implements ElevatorRequest{
    @Override
    public Integer getFloor() {
        return floor;
    }
}
