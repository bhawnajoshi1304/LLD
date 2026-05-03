package com.lld.elevator.model;

public record ExternalRequest(int floor, Direction direction, long timestamp) implements ElevatorRequest {
    
    public ExternalRequest(int floor, Direction direction) {
        this(floor, direction, System.currentTimeMillis());
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
        return direction;
    }
}
