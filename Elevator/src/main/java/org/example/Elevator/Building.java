package org.example.Elevator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Building {
    public String name;
    public int noOfFloors;
    public ElevatorController elevatorController;
}
