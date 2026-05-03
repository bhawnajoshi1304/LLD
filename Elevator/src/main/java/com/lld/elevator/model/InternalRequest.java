package com.lld.elevator.model;

public record InternalRequest(int floor, long timestamp) implements ElevatorRequest {
    
    public InternalRequest(int floor) {
        this(floor, System.currentTimeMillis());
    }
    
    @Override
    public Integer getFloor() {
        return floor;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public void setTimestamp(long timestamp) {
    }
    
    public Direction getDirection() {
        return Direction.IDLE;
    }
}
