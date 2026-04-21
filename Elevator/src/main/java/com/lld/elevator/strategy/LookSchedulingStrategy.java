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

public class LookSchedulingStrategy implements SchedulingStrategy {
    private Queue<Integer> upRequests= new PriorityQueue<>();
    private Queue<Integer> downRequests= new PriorityQueue<>(Collections.reverseOrder());

    @Override
    public void addRequest(ElevatorRequest elevatorRequest, Elevator elevator) {
        if (elevator.getState() instanceof MovingDownState
        ) {
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
        if(hasRequests()) {
            if (elevator.getState() instanceof MovingUpState || elevator.getState() instanceof IdleState || elevator.getState() instanceof StoppedState) {
                Integer next = upRequests.peek();
                if (next != null && next > currentFloor) {
                    return next;
                }
                Integer downNext = downRequests.peek();
                return downNext != null ? downNext : currentFloor;
            } else if (elevator.getState() instanceof MovingDownState) {
                Integer next = downRequests.peek();
                if (next != null && next < currentFloor) {
                    return next;
                }
                Integer upNext = upRequests.peek();
                return upNext != null ? upNext : currentFloor;
            }
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
        
        if (elevator.getState() instanceof MovingUpState || elevator.getState() instanceof IdleState || elevator.getState() instanceof StoppedState) {
            Integer next = upRequests.peek();
            if (next != null && next > currentFloor) {
                return next;
            }
            Integer downNext = downRequests.peek();
            return downNext != null ? downNext : currentFloor;
        } else if(elevator.getState() instanceof MovingDownState){
            Integer next = downRequests.peek();
            if (next != null && next < currentFloor) {
                return next;
            }
            Integer upNext = upRequests.peek();
            return upNext != null ? upNext : currentFloor;
        }
        return currentFloor;
    }

    @Override
    public boolean hasRequests() {
        return !(upRequests.isEmpty() && downRequests.isEmpty());
    }
}
