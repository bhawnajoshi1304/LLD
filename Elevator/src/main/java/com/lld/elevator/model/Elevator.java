package com.lld.elevator.model;

import com.lld.elevator.observer.ElevatorObserver;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
public class Elevator {
    private int id;
    private int floor;
    private Direction direction;
    private ElevatorState state;
    private List<ElevatorObserver> observers;
    private Queue<ElevatorRequest> requests;
    public Queue<ElevatorRequest> getRequestsQueue() {
        return new LinkedList<>(requests);
    }
    public List<ElevatorRequest> getDestinationFloors() {
        return new ArrayList<>(requests);
    }

    public int getId() {
        return this.id;
    }

    public int getCurrentFloor() {
        return floor;
    }

    public void addRequest(ElevatorRequest elevatorRequest) {
        requests.add(elevatorRequest);
    }
}
