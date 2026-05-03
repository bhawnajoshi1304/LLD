package com.lld.elevator.model;

public interface ElevatorRequest{
    Integer getFloor();
    long getTimestamp();
    void setTimestamp(long timestamp);
    Direction getDirection();
}
