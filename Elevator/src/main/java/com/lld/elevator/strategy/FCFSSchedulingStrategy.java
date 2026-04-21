package com.lld.elevator.strategy;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.ElevatorRequest;

import java.util.LinkedList;
import java.util.Queue;

public class FCFSSchedulingStrategy implements SchedulingStrategy{
    private Queue<Integer> requests = new LinkedList<>();
    
    @Override
    public void addRequest(ElevatorRequest elevatorRequest, Elevator elevator) {
        requests.add(elevatorRequest.getFloor());
    }

    @Override
    public int getNextStop(Elevator elevator) {
        if(requests.isEmpty()) return elevator.getCurrentFloor();
        return requests.peek();
    }

    @Override
    public void removeReachedFloor(int floor) {
        if (!requests.isEmpty() && requests.peek() == floor) {
            requests.poll();
        }
    }

    @Override
    public int getNextFloor(Elevator elevator) {
        if(requests.isEmpty()) return elevator.getCurrentFloor();
        
        int nextRequestFloor = requests.peek();
        int currentFloor = elevator.getCurrentFloor();
        
        // FCFS serves requests in order, regardless of direction
        return nextRequestFloor;
    }

    @Override
    public boolean hasRequests() {
        return !requests.isEmpty();
    }
}
