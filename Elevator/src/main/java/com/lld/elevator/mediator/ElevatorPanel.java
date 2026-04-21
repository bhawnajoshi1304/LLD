package com.lld.elevator.mediator;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.InternalRequest;
import com.lld.elevator.mediator.Button;
import java.util.HashMap;
import java.util.Map;

public class ElevatorPanel {
    private final Elevator elevator;

    public ElevatorPanel(Elevator elevator, int totalFloors) {
        this.elevator = elevator;
        Map<Integer, Button> floorButtons = new HashMap<>();
        for (int i = 0; i < totalFloors; i++) {
            Button btn = new Button();
            int finalI = i;
            btn.setOnPressListener(() -> requestFloor(finalI));
            floorButtons.put(i, btn);
        }
    }
    private void requestFloor(int floorNumber) {
        System.out.println("Internal request: Elevator " + elevator.getId() + " to floor " + floorNumber);
        elevator.addRequest(new InternalRequest(floorNumber));
    }

}
