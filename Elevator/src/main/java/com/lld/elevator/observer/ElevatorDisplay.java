package com.lld.elevator.observer;

import com.lld.elevator.state.ElevatorState;

public class ElevatorDisplay implements ElevatorObserver{
    private int currentFloor = 0;
    private String currentState = "IDLE";
    
    @Override
    public void onElevatorFloorChanged(int floor) {
        this.currentFloor = floor;
        displayCombined();
    }

    @Override
    public void onElevatorStateChanged( ElevatorState state) {
        this.currentState = state.getStateName();
        displayCombined();
    }
    
    private void displayCombined() {
        String stateSymbol = switch(currentState){
            case "IDLE" -> "o";
            case "MAINTENANCE" -> "x";
            case "MOVING UP" -> "^";
            case "MOVING DOWN" -> "v";
            case "STOPPED" -> "=";
            default -> "?";
        };
        System.out.println(currentFloor + stateSymbol);
    }
}
