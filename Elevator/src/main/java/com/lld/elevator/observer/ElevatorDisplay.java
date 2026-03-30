package com.lld.elevator.observer;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.ElevatorState;

public class ElevatorDisplay implements ElevatorObserver{
    @Override
    public void onElevatorFloorChanged(Elevator elevator, int floor) {
        System.out.println("Elevator "+elevator.getId()+" moved to "+floor+" floor.");
    }

    @Override
    public void onElevatorStateChanged(Elevator elevator, ElevatorState state) {
        System.out.println("Elevator "+elevator.getId()+" changed state to "+state);
    }
}
