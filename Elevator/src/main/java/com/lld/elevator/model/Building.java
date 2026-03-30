package com.lld.elevator.model;

import com.lld.elevator.service.ElevatorController;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Building {
    public String name;
    public int noOfFloors;
    public ElevatorController elevatorController;
}
