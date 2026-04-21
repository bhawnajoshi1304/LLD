package com.lld.elevator.strategy;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.ElevatorRequest;
import com.lld.elevator.state.IdleState;
import com.lld.elevator.state.MovingDownState;
import com.lld.elevator.state.MovingUpState;
import com.lld.elevator.state.StoppedState;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

public class ScanSchedulingStrategy implements SchedulingStrategy {
    private Queue<Integer> upRequests= new PriorityQueue<>();
    private Queue<Integer> downRequests= new PriorityQueue<>(Collections.reverseOrder());
    
    @Override
    public void addRequest(ElevatorRequest elevatorRequest, Elevator elevator) {
        if (elevator.getState() instanceof MovingDownState) {
            if(elevator.getCurrentFloor() > elevatorRequest.getFloor()) {
                downRequests.add(elevatorRequest.getFloor());
            }
            else {
                upRequests.add(elevatorRequest.getFloor());
            }
        } else if (elevator.getState() instanceof MovingUpState) {
            if(elevator.getCurrentFloor() > elevatorRequest.getFloor()) {
                downRequests.add(elevatorRequest.getFloor());
            }
            else {
                upRequests.add(elevatorRequest.getFloor());
            }
        } else if(elevator.getState() instanceof IdleState || elevator.getState() instanceof StoppedState){
            if(elevator.getCurrentFloor() > elevatorRequest.getFloor()) {
                downRequests.add(elevatorRequest.getFloor());
            }
            else {
                upRequests.add(elevatorRequest.getFloor());
            }
        }
    }

    @Override
    public int getNextStop(Elevator elevator) {
        int currentFloor = elevator.getCurrentFloor();
        if(elevator.getState() instanceof MovingUpState) {
            if (!upRequests.isEmpty()) {
                return upRequests.peek();
            }
            return elevator.getMaxFloor();
        } else if(elevator.getState() instanceof MovingDownState){
            if(!downRequests.isEmpty()) {
                return downRequests.peek();
            }
            return elevator.getMinFloor();
        }
        if (!upRequests.isEmpty()) {
            return upRequests.peek();
        }
        if (!downRequests.isEmpty()) {
            return downRequests.peek();
        }
        return currentFloor;
    }

    @Override
    public void removeReachedFloor(int floor) {
        upRequests.remove(floor);
        downRequests.remove(floor);
    }

    @Override
    public int getNextFloor(Elevator elevator) {
        int currentFloor = elevator.getCurrentFloor();
        
        if(elevator.getState() instanceof MovingUpState) {
            if (!upRequests.isEmpty()) {
                return upRequests.peek();
            }
            return elevator.getMaxFloor();
        } else if(elevator.getState() instanceof MovingDownState){
            if(!downRequests.isEmpty()) {
                return downRequests.peek();
            }
            return elevator.getMinFloor();
        }
        if (!upRequests.isEmpty()) {
            return upRequests.peek();
        }
        if (!downRequests.isEmpty()) {
            return downRequests.peek();
        }
        return currentFloor;
    }

    @Override
    public boolean hasRequests() {
        return !(upRequests.isEmpty() && downRequests.isEmpty());
    }
}
